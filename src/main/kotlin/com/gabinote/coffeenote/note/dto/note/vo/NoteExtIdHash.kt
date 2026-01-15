package com.gabinote.coffeenote.note.dto.note.vo

import com.gabinote.coffeenote.note.domain.note.NoteStatus

data class NoteExtIdHash(
    val externalId: String,
    val hash: String,
    var status: NoteStatus,
)