package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.common.util.time.TimeHelper

object DateField : FieldType() {
    override val key: String
        get() = "DATE"

    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf()

    override fun valueValidation(values: Set<String>): List<FieldTypeValidationResult> {
        val results = mutableListOf<FieldTypeValidationResult>()
        if (values.size != 1) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Date field can has only 1 value"
                )
            )
        }

        val value = values.first()
        if (!TimeHelper.isValidLocalDateTime(value)) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Date field value must be in ISO-8601 format (e.g., 2023-10-05T14:48:00)"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}