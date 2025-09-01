package com.gabinote.coffeenote.common.domain.attribute

data class Attribute(
    val key: String,
    var value: Set<String>
) {
    fun changeValue(newValue: Set<String>) {
        value = newValue
    }
}