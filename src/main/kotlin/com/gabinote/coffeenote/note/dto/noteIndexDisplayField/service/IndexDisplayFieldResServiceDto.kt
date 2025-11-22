package com.gabinote.coffeenote.note.dto.noteIndexDisplayField.service


data class IndexDisplayFieldResServiceDto(
    val name: String,
    val tag: String,
    val value: List<String>,
    val order: Int = 0,
)