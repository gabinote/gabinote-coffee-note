package com.gabinote.coffeenote.note.domain.note

import com.gabinote.coffeenote.field.domain.attribute.Attribute

data class NoteField(
    var id: String,

    var name: String,

    var icon: String,

    var type: String,

    var attributes: Set<Attribute> = emptySet(),

    var order: Int = 0,

    @JvmField
    var isDisplay: Boolean = true,

    var values: Set<String> = emptySet(),
) {
    fun changeAttributes(newAttributes: Set<Attribute>) {
        this.attributes = newAttributes
    }
}