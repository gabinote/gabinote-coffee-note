package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.field.domain.attribute.Attribute

abstract class ListSelectField : FieldType() {

    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf(
        FieldTypeAttributeKey(
            key = "values",
            validationFunc = { value ->
                when {
                    value.size < 2 -> FieldTypeValidationResult(
                        valid = false,
                        message = "At least two options are required. if you want to have only one option, use a Toggle field instead."
                    )

                    value.size > 100 -> FieldTypeValidationResult(
                        valid = false,
                        message = "Maximum number of options is 100."
                    )

                    value.any { it.isEmpty() } -> FieldTypeValidationResult(
                        valid = false,
                        message = "Options cannot be empty."
                    )

                    value.any { it.length > 50 } -> FieldTypeValidationResult(
                        valid = false,
                        message = "Each option cannot exceed 50 characters."
                    )

                    // 중복된 값이 있는지 검사
                    value.toSet().size != value.size -> FieldTypeValidationResult(
                        valid = false,
                        message = "Options cannot have duplicate values."
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

                    value.firstOrEmptyString() !in setOf("true", "false") -> FieldTypeValidationResult(
                        valid = false,
                        message = "AllowAddValue must be either 'true' or 'false'"
                    )

                    else -> FieldTypeValidationResult(valid = true)
                }
            }
        )
    )

    fun getAllowAddValue(attributes: Set<Attribute>): Boolean {
        val source = attributes.find { it.key == "allowAddValue" }
        if (source == null || source.value.size != 1) {
            throw IllegalArgumentException("Invalid allowAddValue attribute")
        }
        return source.value.first() == "true"
    }

    fun getValues(attributes: Set<Attribute>): Set<String> {
        val source = attributes.find { it.key == "values" }
        if (source == null || source.value.size < 2) {
            throw IllegalArgumentException("Invalid values attribute")
        }
        return source.value
    }

}