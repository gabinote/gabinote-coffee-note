package com.gabinote.coffeenote.note.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.nio.ByteBuffer

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
            logger.warn { "Message sent to DLQ: $topic" }
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
            logger.warn { "Message sent to DLQ: $topic" }
        }.onFailure { e ->
            logger.error(e) { "Failed to send message to DLQ" }
        }
    }

    fun sendToDlq(record: ConsumerRecord<String, String>, topic: String, e: Throwable) {
        runCatching {
            val dlqRecord = ProducerRecord(
                topic,
                null,
                record.key(),
                record.value(),
                null
            ).apply {
                applyHeaders(record, this, e)
            }
            kafkaTemplate.send(
                dlqRecord
            ).get()

            logger.warn { "Message sent to DLQ: $topic" }
        }.onFailure { e ->
            logger.error(e) { "Failed to send message to DLQ" }
        }
    }

    private fun applyHeaders(
        origin: ConsumerRecord<String, String>,
        dlqRecord: ProducerRecord<String, String>,
        e: Throwable,
    ) {
        origin.headers().forEach { header ->
            dlqRecord.headers().add(header)
        }

        dlqRecord.headers().apply {
            add("kafka_dlt-original-topic", origin.topic().toByteArray())
            add("kafka_dlt-original-partition", ByteBuffer.allocate(4).putInt(origin.partition()).array())
            add("kafka_dlt-original-offset", ByteBuffer.allocate(8).putLong(origin.offset()).array())
            add("kafka_dlt-exception-fqcn", e.javaClass.name.toByteArray())
            add("kafka_dlt-exception-message", (e.message ?: "No message").toByteArray())
        }

    }
}