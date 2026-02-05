package com.gabinote.coffeenote.common.util.meiliSearch.client.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class IndexRes(
    val uid: String,
    val primaryKey: String?,
    val createdAt: String,
    val updatedAt: String
)
