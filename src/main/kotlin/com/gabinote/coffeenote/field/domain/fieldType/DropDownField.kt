package com.gabinote.coffeenote.field.domain.fieldType

object DropDownField : FieldType() {
    override val key: String
        get() = "DROP_DOWN"

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
                        message = "Maximum number of options is 100."
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

    override fun valueValidation(values: Set<String>): List<FieldTypeValidationResult> {
        val results = mutableListOf<FieldTypeValidationResult>()
        if (values.size != 1) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Dropdown field can has only 1 value"
                )
            )
        }

        val value = values.first()
        if (value.length > 50) {
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