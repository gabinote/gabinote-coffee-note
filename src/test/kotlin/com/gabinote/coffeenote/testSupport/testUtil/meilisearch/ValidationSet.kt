package com.gabinote.coffeenote.testSupport.testUtil.meilisearch

import com.gabinote.coffeenote.common.util.json.annotation.JsonNoArg

@JsonNoArg
data class ValidationSet(
    val uid: String,
    val data: List<Map<String, Any>>,
)