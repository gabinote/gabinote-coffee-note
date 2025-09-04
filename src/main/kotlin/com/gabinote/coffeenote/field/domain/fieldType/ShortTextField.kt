package com.gabinote.coffeenote.field.domain.fieldType

object ShortTextField : TextField() {
    override val key: String
        get() = "SHORT_TEXT"

    override val maxLength: Int = 100
    override val messageTypeName: String = "Short Text"

}