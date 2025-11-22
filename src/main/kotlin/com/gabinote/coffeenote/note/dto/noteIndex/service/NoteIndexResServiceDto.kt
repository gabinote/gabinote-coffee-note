package com.gabinote.coffeenote.note.dto.noteIndex.service

import com.gabinote.coffeenote.note.dto.noteIndexDisplayField.service.IndexDisplayFieldResServiceDto
import java.time.LocalDateTime

data class NoteIndexResServiceDto(
    val id: String,
    val externalId: String,
    var title: String,
    val owner: String,
    val createdDate: LocalDateTime,
    val modifiedDate: LocalDateTime,
    val displayFields: List<IndexDisplayFieldResServiceDto> = emptyList(),
    var filters: Map<String, List<String>>,
)