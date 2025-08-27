package com.gabinote.coffeenote.field.domain.fieldType.type

import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeAttributeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeValidationResult

class TextField : FieldType() {
    override val key: String
        get() = "TEXT"

    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf()

    override fun valueValidation(values: Set<String>): List<FieldTypeValidationResult> {
        val results = mutableListOf<FieldTypeValidationResult>()
        if (values.size != 1) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Text field can has only 1 value"
                )
            )
        }

        val value = values.first()
        if (value.length > 100) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Text field value cannot exceed 100 characters"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}