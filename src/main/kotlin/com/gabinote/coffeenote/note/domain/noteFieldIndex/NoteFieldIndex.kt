package com.gabinote.coffeenote.note.domain.noteFieldIndex

data class NoteFieldIndex(
    // 고유 id (UUID)
    val id: String,
    val owner: String,
    val name: String,
    val value: String,
    val fieldId: String,
    // 노트의 external id
    val noteId: String,
    val synchronizedAt: Long,
    val noteHash: String,
)