package com.gabinote.coffeenote.testSupport.testUtil.data.field

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeAttributeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeValidationResult

object TestFieldType : FieldType() {
    const val INVALID_VALUE = "thisisinvalidvalue"
    override val key: FieldTypeKey = FieldTypeKey.DROP_DOWN
    override val canDisplay: Boolean = true

    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf(
        FieldTypeAttributeKey(key = "isValid", validationFunc = { value ->
            if (value.size != 1 || (value.first() != "true" && value.first() != "ok")) {
                FieldTypeValidationResult(
                    valid = false,
                    message = "not valid attribute"
                )
            } else {
                FieldTypeValidationResult(valid = true)
            }

        }),
    )

    override fun validationValues(
        values: Set<String>,
        attributes: Set<Attribute>,
    ): List<FieldTypeValidationResult> {
        if (values.any { it == INVALID_VALUE }) {
            return listOf(
                FieldTypeValidationResult(
                    valid = false,
                    message = "contains invalid value"
                )
            )
        }
        return listOf(FieldTypeValidationResult(true))
    }
}
