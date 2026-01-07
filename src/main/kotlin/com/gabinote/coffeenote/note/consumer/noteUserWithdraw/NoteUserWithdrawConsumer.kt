package com.gabinote.coffeenote.note.consumer.noteUserWithdraw

import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.note.consumer.ConsumerHelper
import com.gabinote.coffeenote.note.event.userWithdraw.UserWithdrawEvent
import com.gabinote.coffeenote.note.event.userWithdraw.UserWithdrawEventHelper
import com.gabinote.coffeenote.note.service.noteFieldIndex.NoteFieldIndexService
import com.gabinote.coffeenote.note.service.noteIndex.NoteIndexService
import com.gabinote.coffeenote.note.service.noteUserWithdraw.NoteUserWithdrawService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class NoteUserWithdrawConsumer(
    private val objectMapper: ObjectMapper,
    private val noteUserWithdrawService: NoteUserWithdrawService,
    private val consumerHelper: ConsumerHelper,
    private val noteFieldIndexService: NoteFieldIndexService,
    private val noteIndexService: NoteIndexService,
) {
    @KafkaListener(
        topics = [UserWithdrawEventHelper.USER_WITHDRAW_EVENT_TYPE],
        groupId = UserWithdrawEventHelper.USER_WITHDRAW_NOTE_DELETE_GROUP,
    )
    fun deleteWithdrawUserNotes(message: String, ack: Acknowledgment) {
        runCatching {
            val uid = getUidFromMessage(message)
            noteUserWithdrawService.deleteAllNotesByWithdrawUser(uid)
        }.onFailure { e ->
            failback(message, e)
        }
        ack.acknowledge()
    }

    @KafkaListener(
        topics = [UserWithdrawEventHelper.USER_WITHDRAW_EVENT_TYPE],
        groupId = UserWithdrawEventHelper.USER_WITHDRAW_NOTE_INDEX_DELETE_GROUP,
    )
    fun deleteWithdrawUserNoteIndexes(message: String, ack: Acknowledgment) {
        runCatching {
            val owner = getUidFromMessage(message)
            noteIndexService.deleteAllByOwner(owner)
        }.onFailure { e ->
            failback(message, e)
        }
        ack.acknowledge()
    }

    @KafkaListener(
        topics = [UserWithdrawEventHelper.USER_WITHDRAW_EVENT_TYPE],
        groupId = UserWithdrawEventHelper.USER_WITHDRAW_NOTE_FIELD_INDEX_DELETE_GROUP,
    )
    fun deleteWithdrawUserNoteFieldIndexes(message: String, ack: Acknowledgment) {
        runCatching {
            val owner = getUidFromMessage(message)
            noteFieldIndexService.deleteAllByOwner(owner)
        }.onFailure { e ->
            failback(message, e)
        }
        ack.acknowledge()
    }


    private fun failback(message: String, exception: Throwable) {
        logger.error(exception) { "failed to delete notes for withdrawn user. cannot parse message. message = $message" }
        runCatching {
            consumerHelper.sendToDlq(message, UserWithdrawEventHelper.USER_WITHDRAW_EVENT_TYPE_DLQ)
        }.onFailure { e ->
            logger.error(e) { "Retry failed to send to DLQ for message = $message" }
        }
    }

    private fun getUidFromMessage(message: String): String {
        val eventWrapperNode = objectMapper.readTree(message)
        val payloadNode = eventWrapperNode.get("payload").asText()
        val event = objectMapper.readValue(payloadNode, UserWithdrawEvent::class.java)
        return event.uid
    }

}