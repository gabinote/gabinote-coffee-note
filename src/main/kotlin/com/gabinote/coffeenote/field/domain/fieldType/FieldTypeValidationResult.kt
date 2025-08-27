package com.gabinote.coffeenote.field.domain.fieldType

data class FieldTypeValidationResult(
    val valid: Boolean,
    val message: String? = null
)