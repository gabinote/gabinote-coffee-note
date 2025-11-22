package com.gabinote.coffeenote.note.dto.note.service

import com.gabinote.coffeenote.note.dto.noteDisplayField.service.NoteDisplayFieldResServiceDto
import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.util.*

data class NoteListResServiceDto(

    var id: ObjectId,
    var externalId: UUID,
    var title: String,
    var thumbnail: String? = null,
    var createdDate: LocalDateTime,
    var modifiedDate: LocalDateTime,
    var displayFields: List<NoteDisplayFieldResServiceDto> = emptyList(),
    @JvmField
    var isOpen: Boolean,
    var owner: String,
)