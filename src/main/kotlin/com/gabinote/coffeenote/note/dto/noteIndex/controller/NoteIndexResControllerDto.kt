package com.gabinote.coffeenote.note.dto.noteIndex.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.note.dto.noteIndexDisplayField.controller.IndexDisplayFieldResControllerDto
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NoteIndexResControllerDto(
    val externalId: String,
    var title: String,
    val owner: String,
    val createdDate: LocalDateTime,
    val modifiedDate: LocalDateTime,
    val displayFields: List<IndexDisplayFieldResControllerDto> = emptyList(),
    var filters: Map<String, List<String>>,
)