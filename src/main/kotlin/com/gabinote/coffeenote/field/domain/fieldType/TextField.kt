package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.field.domain.attribute.Attribute

abstract class TextField : FieldType() {
    open val maxLength: Int = 100
    open val messageTypeName: String = "Text"
    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf()

    override fun validationValues(values: Set<String>, attributes: Set<Attribute>): List<FieldTypeValidationResult> {
        val results = mutableListOf<FieldTypeValidationResult>()
        if (values.size != 1) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "$messageTypeName field can has only 1 value"
                )
            )
        }

        val value = values.firstOrEmptyString()

        if (value.length > maxLength) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "$messageTypeName field value cannot exceed $maxLength characters"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }


}