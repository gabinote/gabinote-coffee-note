package com.gabinote.coffeenote.common.util.debezium.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.gabinote.coffeenote.common.util.debezium.enums.DebeziumOperation
import com.gabinote.coffeenote.common.util.debezium.json.deserializer.DebeziumOperationDeserializer
import com.gabinote.coffeenote.common.util.debezium.json.serializer.DebeziumOperationSerializer

/**
 * Debezium Change Event 메시지의 페이로드를 나타내는 데이터 클래스
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ChangeMessage(
    val before: String? = null,
    val after: String? = null,
    val source: SourceInfo,
    @JsonSerialize(using = DebeziumOperationSerializer::class)
    @JsonDeserialize(using = DebeziumOperationDeserializer::class)
    val op: DebeziumOperation,
    val tsMs: Long,
    val transaction: TransactionInfo? = null,
)