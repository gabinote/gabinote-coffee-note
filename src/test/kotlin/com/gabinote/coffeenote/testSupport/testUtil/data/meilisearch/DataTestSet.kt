package com.gabinote.coffeenote.testSupport.testUtil.data.meilisearch

import com.gabinote.coffeenote.common.util.json.annotation.JsonNoArg

@JsonNoArg
data class DataTestSet(
    val uid: String,
    val data: List<Map<String, Any>>,
)
