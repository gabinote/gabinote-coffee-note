package com.gabinote.coffeenote.common.util.meiliSearch.client.data

data class MatchesPosition(
    val start: Int = 0,
    val length: Int = 0,
    val indices: List<Int> = emptyList(),
)