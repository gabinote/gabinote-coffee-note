package com.gabinote.coffeenote.common.util.debezium.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 * Debezium Change Event 메시지의 소스 정보를 나타내는 데이터 클래스
 */
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