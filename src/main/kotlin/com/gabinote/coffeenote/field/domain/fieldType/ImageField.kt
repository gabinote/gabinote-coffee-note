package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.field.domain.attribute.Attribute

object ImageField : FieldType() {
    override val key: String
        get() = "IMAGE"

    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf()

    override fun validationValues(values: Set<String>, attributes: Set<Attribute>): List<FieldTypeValidationResult> {
        val results = mutableListOf<FieldTypeValidationResult>()
        if (values.size != 1) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Image field can has only 1 value"
                )
            )
        }

        val value = values.firstOrEmptyString()

        // match with uuid regex
        val uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$".toRegex()
        if (!value.matches(uuidRegex)) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Image field value must be a valid UUID"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}