package com.gabinote.coffeenote.field.domain.fieldType.type

import com.gabinote.coffeenote.common.util.time.TimeHelper
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeAttributeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeValidationResult

class TimeField : FieldType() {
    override val key: String
        get() = "TIME"

    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf(
        // 표시 형식을 12시간제 또는 24시간제로 설정하는 속성
        // "true" (24시간제) 또는 "false" (12시간제) 값을 가짐
        // 저장은 HH:mm 형식으로 저장되며, 표시 형식에 따라 변환하여 보여줌
        FieldTypeAttributeKey(
            key = "24Format",
            validationFunc = { value ->
                when {
                    value.size != 1 -> FieldTypeValidationResult(
                        valid = false,
                        message = "24Format must have exactly 1 value"
                    )

                    value.first() !in setOf("true", "false") -> FieldTypeValidationResult(
                        valid = false,
                        message = "24Format value must be either 'true' or 'false'"
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
                    message = "Time field can has only 1 value"
                )
            )
        }

        val value = values.first()
        // 저장은 HH:mm 형식으로 저장되며, 표시 형식에 따라 변환하여 보여줌
        if (!TimeHelper.isValidTime(value)) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Time field value must be in HH:mm format (e.g., 14:48)"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}