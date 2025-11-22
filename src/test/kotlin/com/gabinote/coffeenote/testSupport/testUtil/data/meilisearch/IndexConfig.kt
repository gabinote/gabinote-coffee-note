package com.gabinote.coffeenote.testSupport.testUtil.data.meilisearch

import com.gabinote.coffeenote.common.util.json.annotation.JsonNoArg

@JsonNoArg
data class IndexConfig(
    val filterableAttributes: List<String>,
    val searchableAttributes: List<String>,
    val faceting: IndexFaceting,
)