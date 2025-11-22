package com.gabinote.coffeenote.note.dto.noteIndexDisplayField.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class IndexDisplayFieldResControllerDto(
    val name: String,
    val tag: String,
    val value: List<String>,
    val order: Int = 0,
)