package com.gabinote.coffeenote.field.domain.fieldType

data class FieldTypeAttributeKey(
    val key: String,
    val validationFunc: (Set<String>) -> FieldTypeValidationResult
)