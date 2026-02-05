package com.gabinote.coffeenote.note.dto.noteDisplayField.service

data class NoteDisplayFieldResServiceDto(
    val name: String,
    val icon: String,
    val values: Set<String>,
    val order: Int = 0,
)