package com.gabinote.coffeenote.field.domain.fieldType.type

import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeAttributeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeValidationResult

class ToggleField : FieldType() {
    override val key: String
        get() = "TOGGLE"

    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf()

    override fun valueValidation(values: Set<String>): List<FieldTypeValidationResult> {
        val results = mutableListOf<FieldTypeValidationResult>()
        if (values.size != 1) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "TOGGLE field can has only 1 value"
                )
            )
        }

        val value = values.first()
        if (value != "true" && value != "false") {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "TOGGLE field value must be either 'true' or 'false'"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}