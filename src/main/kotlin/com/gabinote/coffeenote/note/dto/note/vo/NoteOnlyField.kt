package com.gabinote.coffeenote.note.dto.note.vo

import com.gabinote.coffeenote.note.domain.note.NoteField
import com.gabinote.coffeenote.note.domain.note.NoteStatus

data class NoteOnlyField(
    val externalId: String,
    var fields: List<NoteField> = emptyList(),
    var status: NoteStatus,
)