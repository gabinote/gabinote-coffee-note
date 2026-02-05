package com.gabinote.coffeenote.note.dto.noteFieldIndex.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NoteFieldValueFacetListResControllerDto(
    val fieldName: String,
    val facets: List<NoteFieldValueFacetWithCountResControllerDto> = emptyList(),
)