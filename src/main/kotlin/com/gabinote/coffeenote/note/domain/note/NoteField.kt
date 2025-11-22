package com.gabinote.coffeenote.note.domain.note

data class NoteField(
    var id: String,

    var name: String,

    var icon: String,

    var type: String,

    var attributes: Set<NoteFieldAttribute> = emptySet(),

    var order: Int = 0,

    @JvmField
    var isDisplay: Boolean = true,

    var values: List<String> = emptyList(),
)