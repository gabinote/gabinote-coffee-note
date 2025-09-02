package com.gabinote.coffeenote.field.domain.attribute

data class Attribute(
    val key: String,
    var value: Set<String>
) {
    fun changeValue(newValue: Set<String>) {
        value = newValue
    }
}