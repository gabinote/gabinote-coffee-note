package com.gabinote.coffeenote.field.domain.fieldType

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

                    value.first().length > 50 -> FieldTypeValidationResult(
                        valid = false,
                        message = "Unit must be at most 10 characters long"
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
                    message = "Number field can has only 1 value"
                )
            )
        }

        val value = values.first()
        if (value.length > 100) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Number field value cannot exceed 100 characters"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}