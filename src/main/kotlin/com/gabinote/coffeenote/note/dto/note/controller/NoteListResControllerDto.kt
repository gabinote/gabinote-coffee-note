package com.gabinote.coffeenote.note.dto.note.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.note.dto.noteDisplayField.controller.NoteDisplayFieldResControllerDto
import java.time.LocalDateTime
import java.util.*

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NoteListResControllerDto(

    var externalId: UUID,
    var title: String,
    var thumbnail: String? = null,
    var createdDate: LocalDateTime,
    var modifiedDate: LocalDateTime,
    var displayFields: List<NoteDisplayFieldResControllerDto> = emptyList(),
    @JvmField
    var isOpen: Boolean,
    var owner: String,
)