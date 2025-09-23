package com.gabinote.coffeenote.testSupport.testUtil.data.field

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeAttributeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeValidationResult

object TestFieldTypeCantDisplay : FieldType() {
    override val key: FieldTypeKey = FieldTypeKey.DROP_DOWN
    override val canDisplay: Boolean = false
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
        attributes: Set<Attribute>
    ): List<FieldTypeValidationResult> {
        return listOf(FieldTypeValidationResult(true))
    }
}
