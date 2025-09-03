package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.field.domain.attribute.Attribute

// TODO: 구현 완성
object MultiSelectField : FieldType() {
    override val key: String
        get() = "MULTI_SELECT"

    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf(
        FieldTypeAttributeKey(
            key = "values",
            validationFunc = { value ->
                when {
                    value.size < 2 -> FieldTypeValidationResult(
                        valid = false,
                        message = "At least two options are required. if you want to have only one option, use a Toggle field instead."
                    )

                    value.size < 100 -> FieldTypeValidationResult(
                        valid = false,
                        message = "Maximum number of options is 250."
                    )

                    value.any { it.isEmpty() } -> FieldTypeValidationResult(
                        valid = false,
                        message = "Options cannot be empty."
                    )

                    value.any { it.length > 50 } -> FieldTypeValidationResult(
                        valid = false,
                        message = "Options cannot be empty."
                    )

                    else -> FieldTypeValidationResult(valid = true)
                }
            }
        ),
        FieldTypeAttributeKey(
            key = "allowAddValue",
            validationFunc = { value ->
                when {
                    value.size != 1 -> FieldTypeValidationResult(
                        valid = false,
                        message = "AllowAddValue must be a single string"
                    )

                    value.first() !in setOf("true", "false") -> FieldTypeValidationResult(
                        valid = false,
                        message = "AllowAddValue must be either 'true' or 'false'"
                    )

                    else -> FieldTypeValidationResult(valid = true)
                }
            }
        )
    )

    override fun validationValues(values: Set<String>, attributes: Set<Attribute>): List<FieldTypeValidationResult> {
        val results = mutableListOf<FieldTypeValidationResult>()
        if (values.size > 10) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Multi Select field can have at most 10 values"
                )
            )
        }

        if (values.any { it.length > 50 }) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Dropdown field value cannot exceed 50 characters"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}