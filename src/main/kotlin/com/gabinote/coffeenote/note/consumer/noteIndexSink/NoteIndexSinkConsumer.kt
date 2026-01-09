package com.gabinote.coffeenote.note.consumer.noteIndexSink

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.common.util.debezium.enums.DebeziumOperation
import com.gabinote.coffeenote.common.util.debezium.response.ChangeMessage
import com.gabinote.coffeenote.note.consumer.ConsumerHelper
import com.gabinote.coffeenote.note.consumer.noteIndexSink.data.BeforeAfterNote
import com.gabinote.coffeenote.note.domain.note.Note
import com.gabinote.coffeenote.note.domain.note.NoteStatus
import com.gabinote.coffeenote.note.event.noteCreated.NoteCreateEventHelper.NOTE_CHANGE_TOPIC
import com.gabinote.coffeenote.note.event.noteCreated.NoteCreateEventHelper.NOTE_CHANGE_TOPIC_DLT
import com.gabinote.coffeenote.note.event.noteCreated.NoteCreateEventHelper.NOTE_FIELD_INDEX_GROUP_ID
import com.gabinote.coffeenote.note.event.noteCreated.NoteCreateEventHelper.NOTE_INDEX_GROUP_ID
import com.gabinote.coffeenote.note.service.noteFieldIndex.NoteFieldIndexService
import com.gabinote.coffeenote.note.service.noteIndex.NoteIndexService
import com.gabinote.coffeenote.note.util.convert.noteChangeMessage.NoteChangeMessageConvertHelper
import com.meilisearch.sdk.exceptions.MeilisearchApiException
import com.meilisearch.sdk.exceptions.MeilisearchCommunicationException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.TimeoutException

private val logger = KotlinLogging.logger {}

@Component
class NoteIndexSinkConsumer(
    private val objectMapper: ObjectMapper,
    private val noteIndexService: NoteIndexService,
    private val noteFieldIndexService: NoteFieldIndexService,
    private val consumerHelper: ConsumerHelper,
    private val noteChangeMessageConvertHelper: NoteChangeMessageConvertHelper,
) {


    @KafkaListener(
        topics = [NOTE_CHANGE_TOPIC],
        groupId = NOTE_INDEX_GROUP_ID,
    )
    fun sinkNoteIndex(record: ConsumerRecord<String, String>, ack: Acknowledgment) {
        val message = record.value()
        logger.debug { "Received message for sinkNoteIndex: $message" }

        if (message.isNullOrBlank()) {
            logger.warn { "Received null/empty message, skipping." }
            ack.acknowledge()
            return
        }

        try {
            val changeMessage = parseMessage(message)
            val noteChangeInfo = parseBeforeAfter(changeMessage)

            when (changeMessage.op) {
                DebeziumOperation.CREATE -> upsertNoteIndex(noteChangeInfo)
                DebeziumOperation.UPDATE -> updateNoteIndex(noteChangeInfo)
//                DebeziumOperation.DELETE -> deleteNoteIndex(noteChangeInfo)
                else -> logger.warn { "Unsupported Debezium operation: ${changeMessage.op}" }
            }

            ack.acknowledge()

        } catch (e: Exception) {
            if (isMeiliSearchConnectionError(e)) {
                logger.warn { "Transient error detected (Backoff initiated): ${e.message}" }
                throw e
            } else {
                logger.error(e) { "Fatal error detected (Sent to DLQ): ${e.message}" }
                failbackSink(
                    record,
                    e,
                    NOTE_CHANGE_TOPIC_DLT
                )
                ack.acknowledge()
            }
        }
    }


    @KafkaListener(
        topics = [NOTE_CHANGE_TOPIC],
        groupId = NOTE_FIELD_INDEX_GROUP_ID,
    )
    fun sinkNoteFieldIndex(record: ConsumerRecord<String, String>, ack: Acknowledgment) {
        val message = record.value()
        logger.debug { "Received message for sinkNoteFieldIndex: $message" }

        if (message.isNullOrBlank()) {
            logger.warn { "Received null or empty message for sinkNoteFieldIndex, skipping." }
            ack.acknowledge()
            return
        }

        try {
            val changeMessage = parseMessage(message)
            val noteChangeInfo = parseBeforeAfter(changeMessage)

            when (changeMessage.op) {
                DebeziumOperation.CREATE -> upsertNoteFieldIndex(noteChangeInfo)
                DebeziumOperation.UPDATE -> updateNoteFieldIndex(noteChangeInfo)
//                DebeziumOperation.DELETE -> deleteNoteFieldIndex(noteChangeInfo)
                else -> logger.warn { "Unsupported Debezium operation: ${changeMessage.op}" }
            }
            ack.acknowledge()

        } catch (e: Exception) {
            if (isMeiliSearchConnectionError(e)) {
                logger.warn(e) { "Transient error detected in sinkNoteFieldIndex. Initiating Backoff. Error: ${e.message}" }
                throw e
            } else {
                logger.error(e) { "Fatal error detected in sinkNoteFieldIndex. Sending to DLQ and skipping. Error: ${e.message}" }
                failbackSink(
                    record,
                    e,
                    NOTE_CHANGE_TOPIC_DLT
                )
                ack.acknowledge()
            }
        }
    }

    private fun updateNoteIndex(message: BeforeAfterNote) {
        when {
            checkIsDeleted(message) -> {
                logger.debug { "Deleted note index note=${message.after}" }
                deleteNoteIndex(message)
                return
            }

            checkIsNotUpdated(before = message.before, after = message.after) -> {
                logger.debug { "Note index not updated for note=${message.after ?: "none"}, skipping." }
                return
            }

            else -> {
                upsertNoteIndex(message)
            }
        }
    }

    // 신규 노트 인덱스 생성 혹은 기존 노트 인덱스 업데이트
    private fun upsertNoteIndex(message: BeforeAfterNote) {
        val note = message.after ?: throw IllegalArgumentException("parsed note after is null")
        logger.debug { "Upsert note index for note externalId=${note.externalId} Note=$note" }
        noteIndexService.createFromNote(note)
    }

    private fun checkIsDeleted(message: BeforeAfterNote): Boolean {
        val after = message.after ?: throw IllegalArgumentException("parsed note after is null")
        return after.status == NoteStatus.DELETED
    }

    private fun deleteNoteIndex(message: BeforeAfterNote) {
        val note = message.before ?: throw IllegalArgumentException("parsed note before is null")
        val noteId = note.externalId
        logger.debug { "Delete note index for note externalId=${noteId}" }
        noteIndexService.deleteByNoteId(UUID.fromString(noteId))
    }

    private fun updateNoteFieldIndex(message: BeforeAfterNote) {
        when {
            checkIsDeleted(message) -> {
                logger.debug { "Deleted note index note=${message.after}" }
                deleteNoteFieldIndex(message)
                return
            }

            checkIsNotUpdated(before = message.before, after = message.after) -> {
                logger.debug { "Note index not updated for note=${message.after ?: "none"}, skipping." }
                return
            }

            else -> {
                upsertNoteFieldIndex(message)
            }
        }
    }

    private fun upsertNoteFieldIndex(message: BeforeAfterNote) {
        val note = message.after ?: throw IllegalArgumentException("parsed note after is null")
        logger.debug { "Upsert note field index for note externalId=${note.externalId} Note=$note" }
        val noteId = note.externalId
        noteFieldIndexService.deleteByNoteExtId(UUID.fromString(noteId))
        noteFieldIndexService.createFromNote(note)
    }

    private fun deleteNoteFieldIndex(message: BeforeAfterNote) {
        val note = message.before ?: throw IllegalArgumentException("parsed note before is null")
        val noteId = note.externalId
        logger.debug { "Delete note field index for note externalId=${noteId}" }
        noteFieldIndexService.deleteByNoteExtId(UUID.fromString(noteId))
    }

    private fun checkIsNotUpdated(before: Note?, after: Note?): Boolean {
        if (before == null || after == null) throw IllegalArgumentException("update operation must have both before and after note. before=$before, after=$after")
        return before.hash == after.hash
    }

    private fun failbackSink(record: ConsumerRecord<String, String>, exception: Throwable, dltTopic: String) {
        logger.error(exception) { "failed to sink note index. cannot parse message. message = ${record.value()}" }
        runCatching {
            consumerHelper.sendToDlq(
                record,
                dltTopic,
                exception
            )
        }.onFailure { e ->
            logger.error(e) { "Retry failed to send to DLQ for message = ${record.value()}" }
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

    private fun isMeiliSearchConnectionError(e: Throwable): Boolean {
        return when (e) {
            // DNS, 타임아웃, 연결 거부
            is MeilisearchCommunicationException -> true

            is MeilisearchApiException -> {

                val type = e.type
                // https://www.meilisearch.com/docs/reference/errors/overview#errors
                // 5xx 에러 type들
                return type == "internal" || type == "system"
            }

            // java 내장 예외들
            is ConnectException,
            is SocketTimeoutException,
            is TimeoutException,
                -> true

            else -> false
        }
    }

}