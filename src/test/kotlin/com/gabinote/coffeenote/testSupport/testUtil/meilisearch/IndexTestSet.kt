package com.gabinote.coffeenote.testSupport.testUtil.meilisearch

import com.gabinote.coffeenote.common.util.json.annotation.JsonNoArg

@JsonNoArg
data class IndexTestSet(
    val uid: String,
    val primaryKey: String,
    val config: IndexConfig,
)
