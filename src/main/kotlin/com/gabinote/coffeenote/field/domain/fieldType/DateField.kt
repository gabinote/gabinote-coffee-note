package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.common.util.time.TimeHelper
import com.gabinote.coffeenote.field.domain.attribute.Attribute

object DateField : FieldType() {
    override val key: String
        get() = "DATE"

    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf()

    override fun validationValues(values: Set<String>, attributes: Set<Attribute>): List<FieldTypeValidationResult> {
        val results = mutableListOf<FieldTypeValidationResult>()
        if (values.size != 1) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Date field can has only 1 value"
                )
            )
        }

        val value = values.firstOrEmptyString()
        if (!TimeHelper.isValidLocalDate(input = value)) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Date field value must be in ISO_LOCAL_DATE format (yyyy-MM-dd)"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}