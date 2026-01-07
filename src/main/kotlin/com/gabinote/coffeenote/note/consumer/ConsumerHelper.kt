package com.gabinote.coffeenote.note.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.note.event.noteCreated.NoteCreateEventHelper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class ConsumerHelper(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    fun sendToDlq(message: Any, topic: String) {
        runCatching {
            val messageString = objectMapper.writeValueAsString(message)
            kafkaTemplate.send(
                topic,
                messageString,
            ).get()
            logger.warn { "Message sent to DLQ: ${NoteCreateEventHelper.NOTE_CREATED_TOPIC_DLQ}" }
        }.onFailure { e ->
            logger.error(e) { "Failed to send message to DLQ" }
        }
    }

    fun sendToDlq(message: String, topic: String) {
        runCatching {
            kafkaTemplate.send(
                topic,
                message
            ).get()
            logger.warn { "Message sent to DLQ: ${NoteCreateEventHelper.NOTE_CREATED_TOPIC_DLQ}" }
        }.onFailure { e ->
            logger.error(e) { "Failed to send message to DLQ" }
        }
    }
}