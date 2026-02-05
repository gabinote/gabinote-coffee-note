package com.gabinote.coffeenote.common.util.meiliSearch.client.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PagedIndexRes(
    val results: List<IndexRes> = emptyList(),
    val offset: Int,
    val limit: Int,
    val total: Int
)
