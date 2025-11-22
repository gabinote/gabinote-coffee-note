package com.gabinote.coffeenote.note.dto.noteDisplayField.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NoteDisplayFieldResControllerDto(
    val name: String,
    val icon: String,
    val values: Set<String>,
    val order: Int = 0,
)