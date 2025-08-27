package com.gabinote.coffeenote.field.domain.fieldType.type

import com.gabinote.coffeenote.common.util.time.TimeHelper
import com.gabinote.coffeenote.common.util.type.TypeCheckHelper
import com.gabinote.coffeenote.common.util.type.TypeCheckHelper.isInt
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeAttributeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeValidationResult

class ScoreField : FieldType() {
    override val key: String
        get() = "SCORE"

    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf(
        FieldTypeAttributeKey(
            key = "maxScore",
            validationFunc = { value ->
                when {
                    value.size != 1 -> FieldTypeValidationResult(
                        valid = false,
                        message = "maxScore must have exactly 1 value"
                    )

                    !isInt(value.first()) -> FieldTypeValidationResult(
                        valid = false,
                        message = "maxScore value must be an integer"
                    )

                    value.first().toInt() > 10 -> FieldTypeValidationResult(
                        valid = false,
                        message = "maxScore value cannot be greater than 10"
                    )

                    value.first().toInt() < 3 -> FieldTypeValidationResult(
                        valid = false,
                        message = "maxScore value cannot be less than 3"
                    )

                    else -> FieldTypeValidationResult(valid = true)
                }
            }
        ),
    )

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