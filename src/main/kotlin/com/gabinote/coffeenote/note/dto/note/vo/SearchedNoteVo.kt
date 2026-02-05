package com.gabinote.coffeenote.note.dto.note.vo

data class SearchedNoteVo(
    val name: String,
    val icon: String,
    val type: String,
    val values: List<String> = emptyList(),
)