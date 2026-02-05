package com.gabinote.coffeenote.note.dto.note.service

import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldCreateReqServiceDto

data class NoteCreateReqServiceDto(
    var title: String,
    var thumbnail: String? = null,
    var fields: List<NoteFieldCreateReqServiceDto>,
    @JvmField
    var isOpen: Boolean,
    var owner: String,
)