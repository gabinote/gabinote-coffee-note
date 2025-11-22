package com.gabinote.coffeenote.note.domain.note

data class NoteFieldAttribute(
    val key: String,
    var value: Set<String>,
)