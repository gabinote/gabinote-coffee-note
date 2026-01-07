package com.gabinote.coffeenote.common.util.debezium.json.parser

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.common.util.time.TimeProvider
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime

@Component
class MongoDateParser(
    private val timeProvider: TimeProvider,
    private val objectMapper: ObjectMapper,
) {

    fun parseDateTime(
        dateTimeString: String,
    ): LocalDateTime {
        val node = objectMapper.readTree(dateTimeString)
        return parseDateTime(node)
    }

    /**
     * JSON 노드에서 날짜/시간 필드를 파싱
     * 지원 형식:
     * 1. {"$date": 1696543200000} (숫자 타임스탬프)
     * 2. {"$date": "1696543200000"} (문자열 타임스탬프)
     * 3. {"$date": "2023-10-05T12:00:00Z"} (ISO-8601 문자열 - Relaxed Mode)
     * 4. 1696543200000 (직접 숫자 타임스탬프)
     * 5. "2023-10-05T12:00:00" (직접 ISO-8601 문자열)
     */
    fun parseDateTime(node: JsonNode): LocalDateTime {
        // 1. $date 래퍼가 있으면 벗겨내고, 없으면 노드 자체를 사용
        val valueNode = if (node.has("\$date")) node.get("\$date") else node

        return when {
            // Case A: 숫자형 (밀리초 타임스탬프)
            valueNode.isNumber -> {
                millisToLocalDateTime(valueNode.asLong())
            }

            // Case B: 문자열형
            valueNode.isTextual -> {
                val text = valueNode.asText()
                // 1. 문자열이지만 숫자로 된 타임스탬프인지 확인 ("169654...")
                val millis = text.toLongOrNull()

                if (millis != null) {
                    millisToLocalDateTime(millis)
                } else {
                    // 2. 숫자가 아니면 ISO-8601 형식으로 파싱 ("2023-10-...")
                    isoToLocalDateTime(text)
                }
            }

            // Case C: null이거나 지원하지 않는 형식
            else -> throw IllegalArgumentException("지원하지 않는 날짜 데이터 형식입니다: $node")
        }
    }

    // [헬퍼] 타임스탬프(Long) -> LocalDateTime 변환
    private fun millisToLocalDateTime(millis: Long): LocalDateTime {
        return Instant.ofEpochMilli(millis)
            .atZone(timeProvider.zoneOffset()) // timeProvider의 Zone 정보 사용
            .toLocalDateTime()
    }

    // [헬퍼] ISO 문자열(String) -> LocalDateTime 변환
    private fun isoToLocalDateTime(isoString: String): LocalDateTime {
        // MongoDB Relaxed Mode는 보통 "2023-10-05T12:00:00Z" 처럼 UTC(Z)가 붙어서 옴
        // 따라서 Instant.parse로 안전하게 파싱 후 TimeProvider의 Zone으로 변환
        return try {
            Instant.parse(isoString)
                .atZone(timeProvider.zoneOffset())
                .toLocalDateTime()
        } catch (e: Exception) {
            // 혹시 Z가 없는 로컬 형식("2023-10-05T12:00:00")으로 올 경우 대비
            LocalDateTime.parse(isoString)
        }
    }

}