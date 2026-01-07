package com.gabinote.coffeenote.common.util.debezium.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TransactionInfo(
    val id: String,
    val totalOrder: Long,
    val dataCollectionOrder: Long,
)