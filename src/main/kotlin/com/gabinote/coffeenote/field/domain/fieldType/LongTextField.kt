package com.gabinote.coffeenote.field.domain.fieldType

object LongTextField : TextField() {
    override val key: String
        get() = "LONG_TEXT"

    override val maxLength: Int = 10000
    override val messageTypeName: String = "Long Text"

}