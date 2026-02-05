package com.gabinote.coffeenote.note.dto.noteFieldIndex.vo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class NoteFieldIndexNoteIdHash(
    val id: String,
    val noteId: String,
    val name: String,
    val value: String,
    val fieldId: String,
)