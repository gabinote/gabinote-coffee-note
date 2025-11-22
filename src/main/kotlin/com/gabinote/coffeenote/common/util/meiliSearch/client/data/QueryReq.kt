package com.gabinote.coffeenote.common.util.meiliSearch.client.data

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class QueryReq(
    val q: String? = null,
    val limit: Int? = null,
    val offset: Int? = null,
    val attributesToCrop: List<String>? = null,
    val attributesToHighlight: List<String>? = null,
    val cropLength: Int? = null,
    val showMatchesPosition: Boolean? = null,
    val facets: List<String>? = null,
    val filters: String? = null,
    val filter: List<String>? = null,
)