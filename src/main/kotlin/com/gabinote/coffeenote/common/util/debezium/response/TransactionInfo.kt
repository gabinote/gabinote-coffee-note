package com.gabinote.coffeenote.common.util.debezium.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 * Debezium Change Event 메시지의 트랜잭션 정보를 나타내는 데이터 클래스
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TransactionInfo(
    val id: String,
    val totalOrder: Long,
    val dataCollectionOrder: Long,
)