package com.gabinote.coffeenote.field.util.validation.fieldType

import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeValidationResult

object FieldTypeValidationResultHelper {
    fun List<FieldTypeValidationResult>.isValid(): Boolean {
        return this.all { it.valid }
    }

    fun List<FieldTypeValidationResult>.failures(): List<String> {
        return this.filter { !it.valid }.mapNotNull { it.message }
    }
}