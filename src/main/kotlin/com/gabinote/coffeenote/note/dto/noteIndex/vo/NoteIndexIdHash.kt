package com.gabinote.coffeenote.note.dto.noteIndex.vo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class NoteIndexIdHash(
    val id: String,
    val noteHash: String,
)