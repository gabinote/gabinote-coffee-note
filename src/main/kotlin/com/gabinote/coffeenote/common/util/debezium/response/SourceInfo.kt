package com.gabinote.coffeenote.common.util.debezium.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SourceInfo(
    val version: String,
    val connector: String,
    val name: String,
    val tsMs: Long,
    val snapshot: String,
    val db: String,
    val collection: String,
    val ord: Int,
    val lsid: String? = null,
    val txnNumber: Long? = null,
)