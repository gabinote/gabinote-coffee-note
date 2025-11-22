package com.gabinote.coffeenote.testSupport.testUtil.data.meilisearch

import com.gabinote.coffeenote.common.util.json.annotation.JsonNoArg

@JsonNoArg
data class IndexTestSet(
    val uid: String,
    val primaryKey: String,
    val config: IndexConfig,
)
