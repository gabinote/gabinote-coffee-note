package com.gabinote.coffeenote.note.domain.noteFieldIndex

data class NoteFieldIndex(
    val id: String,
    val owner: String,
    val name: String,
    val value: String,
    val noteId: String,
    val synchronizedAt: Long,
)