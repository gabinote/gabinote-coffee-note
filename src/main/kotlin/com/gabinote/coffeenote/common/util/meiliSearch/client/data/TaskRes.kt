package com.gabinote.coffeenote.common.util.meiliSearch.client.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskRes(
    val taskUid: Long,
    val indexUid: String,
    val status: String,
    val type: String,
    val enqueuedAt: String,
)