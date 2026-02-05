package com.gabinote.coffeenote.note.consumer.noteUserWithdraw

import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.note.consumer.ConsumerHelper
import com.gabinote.coffeenote.note.event.userWithdraw.UserWithdrawEvent
import com.gabinote.coffeenote.note.event.userWithdraw.UserWithdrawEventHelper
import com.gabinote.coffeenote.note.event.userWithdraw.WithdrawProcess
import com.gabinote.coffeenote.note.service.noteUserWithdraw.NoteUserWithdrawService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class NoteUserWithdrawConsumer(
    private val objectMapper: ObjectMapper,
    private val noteUserWithdrawService: NoteUserWithdrawService,
    private val consumerHelper: ConsumerHelper,
) {
    @KafkaListener(
        topics = [UserWithdrawEventHelper.USER_WITHDRAW_EVENT_TYPE],
        groupId = UserWithdrawEventHelper.USER_WITHDRAW_NOTE_DELETE_GROUP,
    )
    fun deleteWithdrawUserNotes(record: ConsumerRecord<String, String>, ack: Acknowledgment) {
        val message = record.value()
        logger.debug { "get deleteWithdrawUserNotes message $message" }
        if (message.isNullOrBlank()) {
            logger.warn { "Received null/empty message, skipping." }
            ack.acknowledge()
            return
        }
        runCatching {
            val uid = getUidFromMessage(message)
            noteUserWithdrawService.deleteAllNotesByWithdrawUser(uid)
        }.onFailure { e ->
            failback(record, WithdrawProcess.NOTE_DELETE, e)
        }
        ack.acknowledge()
    }

    @KafkaListener(
        topics = [UserWithdrawEventHelper.USER_WITHDRAW_EVENT_TYPE],
        groupId = UserWithdrawEventHelper.USER_WITHDRAW_NOTE_INDEX_DELETE_GROUP,
    )
    fun deleteWithdrawUserNoteIndexes(record: ConsumerRecord<String, String>, ack: Acknowledgment) {
        val message = record.value()
        logger.debug { "get deleteWithdrawUserNoteIndexes message $message" }
        if (message.isNullOrBlank()) {
            logger.warn { "Received null/empty message, skipping." }
            ack.acknowledge()
            return
        }
        runCatching {
            val owner = getUidFromMessage(message)
            noteUserWithdrawService.deleteAllNoteIndexesByWithdrawUser(owner)
        }.onFailure { e ->
            failback(record, WithdrawProcess.NOTE_INDEX_DELETE, e)
        }
        ack.acknowledge()
    }

    @KafkaListener(
        topics = [UserWithdrawEventHelper.USER_WITHDRAW_EVENT_TYPE],
        groupId = UserWithdrawEventHelper.USER_WITHDRAW_NOTE_FIELD_INDEX_DELETE_GROUP,
    )
    fun deleteWithdrawUserNoteFieldIndexes(record: ConsumerRecord<String, String>, ack: Acknowledgment) {
        val message = record.value()
        logger.debug { "get deleteWithdrawUserNoteFieldIndexes message $message" }
        if (message.isNullOrBlank()) {
            logger.warn { "Received null/empty message, skipping." }
            ack.acknowledge()
            return
        }
        runCatching {
            val owner = getUidFromMessage(message)
            noteUserWithdrawService.deleteAllNoteFieldsIndexesByWithdrawUser(owner)
        }.onFailure { e ->
            failback(record, WithdrawProcess.NOTE_FIELD_INDEX_DELETE, e)
        }
        ack.acknowledge()
    }


    private fun failback(record: ConsumerRecord<String, String>, process: WithdrawProcess, exception: Throwable) {
        logger.error(exception) { "failed to ${process.value} for withdrawn user. cannot parse message. message = ${record.value()}" }
        runCatching {
            consumerHelper.sendToDlq(
                record,
                UserWithdrawEventHelper.USER_WITHDRAW_EVENT_TYPE_DLQ,
                exception
            )

            noteUserWithdrawService.createDeleteNoteFailHistory(getUidFromMessage(record.value()), process)

        }.onFailure { e ->
            logger.error(e) { "Retry failed to send to DLQ for message = ${record.value()}" }
        }
    }

    private fun getUidFromMessage(message: String): String {
        val eventWrapperNode = objectMapper.readTree(message)
        val payloadNode = eventWrapperNode.get("payload").asText()
        val event = objectMapper.readValue(payloadNode, UserWithdrawEvent::class.java)
        return event.uid
    }

}