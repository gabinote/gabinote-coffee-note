package com.gabinote.coffeenote.note.consumer.noteIndexSink

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.common.util.debezium.enums.DebeziumOperation
import com.gabinote.coffeenote.common.util.debezium.response.ChangeMessage
import com.gabinote.coffeenote.note.consumer.ConsumerHelper
import com.gabinote.coffeenote.note.consumer.noteIndexSink.data.BeforeAfterNote
import com.gabinote.coffeenote.note.domain.note.Note
import com.gabinote.coffeenote.note.event.noteCreated.NoteCreateEventHelper.NOTE_CHANGE_TOPIC
import com.gabinote.coffeenote.note.event.noteCreated.NoteCreateEventHelper.NOTE_FIELD_INDEX_GROUP_ID
import com.gabinote.coffeenote.note.event.noteCreated.NoteCreateEventHelper.NOTE_INDEX_GROUP_ID
import com.gabinote.coffeenote.note.event.userWithdraw.UserWithdrawEventHelper
import com.gabinote.coffeenote.note.service.note.NoteService
import com.gabinote.coffeenote.note.service.noteFieldIndex.NoteFieldIndexService
import com.gabinote.coffeenote.note.service.noteIndex.NoteIndexService
import com.gabinote.coffeenote.note.util.convert.noteChangeMessage.NoteChangeMessageConvertHelper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class NoteIndexSinkConsumer(
    private val objectMapper: ObjectMapper,
    private val noteIndexService: NoteIndexService,
    private val noteFieldIndexService: NoteFieldIndexService,
    private val noteService: NoteService,
    private val consumerHelper: ConsumerHelper,
    private val noteChangeMessageConvertHelper: NoteChangeMessageConvertHelper,
) {


    @KafkaListener(
        topics = [NOTE_CHANGE_TOPIC],
        groupId = NOTE_INDEX_GROUP_ID,
    )
    fun sinkNoteIndex(message: String?, ack: Acknowledgment) {
        logger.debug { "Received message for sinkNoteIndex: $message" }
        message ?: run {
            logger.warn { "Received null message for sinkNoteFieldIndex, skipping." }
            ack.acknowledge()
            return
        }
        runCatching {
            val changeMessage = parseMessage(message)
            val noteChangeInfo = parseBeforeAfter(changeMessage)
            when (changeMessage.op) {
                DebeziumOperation.CREATE -> upsertNoteIndex(noteChangeInfo)
                DebeziumOperation.UPDATE -> updateNoteIndex(noteChangeInfo)
                DebeziumOperation.DELETE -> deleteNoteIndex(noteChangeInfo)
                else -> logger.warn { "Unsupported Debezium operation: ${changeMessage.op}" }
            }
        }.onFailure { e ->
            failbackSinkNoteIndex(message, e)
        }
        ack.acknowledge()
    }

    fun updateNoteIndex(message: BeforeAfterNote) {
        checkIsUpdated(before = message.before, after = message.after).let { isUpdated ->
            if (!isUpdated) {
                logger.debug { "Note field index not updated for note=${message.after ?: "none"}, skipping." }
                return
            }
            upsertNoteIndex(message)
        }
    }

    // 신규 노트 인덱스 생성 혹은 기존 노트 인덱스 업데이트
    fun upsertNoteIndex(message: BeforeAfterNote) {
        val note = message.after ?: throw IllegalArgumentException("parsed note after is null")
        logger.debug { "Upsert note index for note externalId=${note.externalId} Note=$note" }
        noteIndexService.createFromNote(note)
    }

    fun deleteNoteIndex(message: BeforeAfterNote) {
        val note = message.before ?: throw IllegalArgumentException("parsed note before is null")
        val noteId = note.externalId
        logger.debug { "Delete note index for note externalId=${noteId}" }
        noteIndexService.deleteByNoteId(UUID.fromString(noteId))
    }


    @KafkaListener(
        topics = [NOTE_CHANGE_TOPIC],
        groupId = NOTE_FIELD_INDEX_GROUP_ID,
    )
    fun sinkNoteFieldIndex(message: String?, ack: Acknowledgment) {
        logger.debug { "Received message for sinkNoteFieldIndex: $message" }
        message ?: run {
            logger.warn { "Received null message for sinkNoteFieldIndex, skipping." }
            ack.acknowledge()
            return
        }
        runCatching {
            val changeMessage = parseMessage(message)
            val noteChangeInfo = parseBeforeAfter(changeMessage)
            when (changeMessage.op) {
                DebeziumOperation.CREATE -> upsertNoteFieldIndex(noteChangeInfo)
                DebeziumOperation.UPDATE -> updateNoteFieldIndex(noteChangeInfo)
                DebeziumOperation.DELETE -> deleteNoteFieldIndex(noteChangeInfo)
                else -> logger.warn { "Unsupported Debezium operation: ${changeMessage.op}" }
            }
        }.onFailure { e ->
            failbackSinkNoteFieldIndex(message, e)
        }
        ack.acknowledge()
    }

    fun updateNoteFieldIndex(message: BeforeAfterNote) {
        checkIsUpdated(before = message.before, after = message.after).let { isUpdated ->
            if (!isUpdated) {
                logger.debug { "Note field index not updated for note=${message.after ?: "none"}, skipping." }
                return
            }
        }
        upsertNoteFieldIndex(message)
    }

    fun upsertNoteFieldIndex(message: BeforeAfterNote) {
        val note = message.after ?: throw IllegalArgumentException("parsed note after is null")
        logger.debug { "Upsert note field index for note externalId=${note.externalId} Note=$note" }
        val noteId = note.externalId
        noteFieldIndexService.deleteByNoteExtId(UUID.fromString(noteId))
        noteFieldIndexService.createFromNote(note)
    }

    fun deleteNoteFieldIndex(message: BeforeAfterNote) {
        val note = message.before ?: throw IllegalArgumentException("parsed note before is null")
        val noteId = note.externalId
        logger.debug { "Delete note field index for note externalId=${noteId}" }
        noteFieldIndexService.deleteByNoteExtId(UUID.fromString(noteId))
    }

    private fun checkIsUpdated(before: Note?, after: Note?): Boolean {
        if (before == null || after == null) throw IllegalArgumentException("update operation must have both before and after note. before=$before, after=$after")
        return before.hash != after.hash
    }

    private fun failbackSinkNoteIndex(message: String, exception: Throwable) {
        logger.error(exception) { "failed to sink note index. cannot parse message. message = $message" }
        runCatching {
            consumerHelper.sendToDlq(message, UserWithdrawEventHelper.USER_WITHDRAW_EVENT_TYPE_DLQ)
        }.onFailure { e ->
            logger.error(e) { "Retry failed to send to DLQ for message = $message" }
        }
    }


    private fun failbackSinkNoteFieldIndex(message: String, exception: Throwable) {
        logger.error(exception) { "failed to sink note field index. cannot parse message. message = $message" }
        runCatching {
            consumerHelper.sendToDlq(message, UserWithdrawEventHelper.USER_WITHDRAW_EVENT_TYPE_DLQ)
        }.onFailure { e ->
            logger.error(e) { "Retry failed to send to DLQ for message = $message" }
        }
    }


    private fun parseMessage(message: String): ChangeMessage {
        return objectMapper.readValue(
            message,
            object : TypeReference<ChangeMessage>() {}
        )
    }

    private fun parseBeforeAfter(message: ChangeMessage): BeforeAfterNote {
        val before = message.before?.let {
            noteChangeMessageConvertHelper.parseFromChangeMessageJson(it)
        }

        val after = message.after?.let {
            noteChangeMessageConvertHelper.parseFromChangeMessageJson(it)
        }

        return BeforeAfterNote(before = before, after = after)
    }


}