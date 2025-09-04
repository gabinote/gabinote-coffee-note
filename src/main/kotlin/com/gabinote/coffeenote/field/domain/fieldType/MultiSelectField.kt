package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.field.domain.attribute.Attribute

object MultiSelectField : ListSelectField() {
    override val key: String
        get() = "MULTI_SELECT"

    override fun validationValues(values: Set<String>, attributes: Set<Attribute>): List<FieldTypeValidationResult> {
        val results = mutableListOf<FieldTypeValidationResult>()
        if (values.size > 30) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Multi Select field can have at most 30 values"
                )
            )
        }

        if (values.any { it.length > 50 }) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Multi Select field value cannot exceed 50 characters"
                )
            )
        }

        if (values.any { it.isBlank() }) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Multi Select field value cannot be empty"
                )
            )
        }

        val allowAddValue = getAllowAddValue(attributes)
        val allowValues = getValues(attributes)

        if (!allowAddValue && values.any { it !in allowValues }) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "The value is not in the list of allowed values, and adding new values is not permitted."
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}