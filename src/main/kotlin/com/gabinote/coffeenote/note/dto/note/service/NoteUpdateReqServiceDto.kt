package com.gabinote.coffeenote.note.dto.note.service

import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldCreateReqServiceDto
import java.util.*

data class NoteUpdateReqServiceDto(
    var externalId: UUID,
    var title: String,
    var thumbnail: String? = null,
    var fields: List<NoteFieldCreateReqServiceDto> = emptyList(),
    @JvmField
    var isOpen: Boolean,
    var owner: String,
)