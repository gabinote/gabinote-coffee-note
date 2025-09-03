package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.field.domain.attribute.Attribute

object NumberField : FieldType() {
    override val key: String
        get() = "NUMBER"

    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf(
        FieldTypeAttributeKey(
            key = "unit",
            validationFunc = { value ->
                when {
                    value.size != 1 -> FieldTypeValidationResult(
                        valid = false,
                        message = "Unit must be a single non-empty string"
                    )

                    value.firstOrEmptyString().length > 50 -> FieldTypeValidationResult(
                        valid = false,
                        message = "Unit must be at most 10 characters long"
                    )

                    value.firstOrEmptyString().isBlank() -> FieldTypeValidationResult(
                        valid = false,
                        message = "Unit must not be empty or blank"
                    )

                    else -> FieldTypeValidationResult(valid = true)
                }
            }
        )
    )

    override fun validationValues(values: Set<String>, attributes: Set<Attribute>): List<FieldTypeValidationResult> {
        val results = mutableListOf<FieldTypeValidationResult>()
        if (values.size != 1) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Number field can has only 1 value"
                )
            )
        }

        val value = values.firstOrEmptyString()
        if (value.length > 50) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Number field value cannot exceed 50 characters"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}