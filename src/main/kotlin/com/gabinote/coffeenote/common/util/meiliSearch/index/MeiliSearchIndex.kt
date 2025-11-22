package com.gabinote.coffeenote.common.util.meiliSearch.index

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MeiliSearchIndex(
    val indexName: String,
    val primaryKey: String = "id"
)