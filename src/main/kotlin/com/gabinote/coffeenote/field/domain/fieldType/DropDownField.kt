package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.field.domain.attribute.Attribute

object DropDownField : ListSelectField() {
    override val key: String
        get() = "DROP_DOWN"

//    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf(
//        FieldTypeAttributeKey(
//            key = "values",
//            validationFunc = { value ->
//                when {
//                    value.size < 2 -> FieldTypeValidationResult(
//                        valid = false,
//                        message = "At least two options are required. if you want to have only one option, use a Toggle field instead."
//                    )
//
//                    value.size > 100 -> FieldTypeValidationResult(
//                        valid = false,
//                        message = "Maximum number of options is 100."
//                    )
//
//                    value.any { it.isEmpty() } -> FieldTypeValidationResult(
//                        valid = false,
//                        message = "Options cannot be empty."
//                    )
//
//                    value.any { it.length > 50 } -> FieldTypeValidationResult(
//                        valid = false,
//                        message = "Each option cannot exceed 50 characters."
//                    )
//
//                    // 중복된 값이 있는지 검사
//                    value.toSet().size != value.size -> FieldTypeValidationResult(
//                        valid = false,
//                        message = "Options cannot have duplicate values."
//                    )
//
//                    else -> FieldTypeValidationResult(valid = true)
//                }
//            }
//        ),
//        FieldTypeAttributeKey(
//            key = "allowAddValue",
//            validationFunc = { value ->
//                when {
//                    value.size != 1 -> FieldTypeValidationResult(
//                        valid = false,
//                        message = "AllowAddValue must be a single string"
//                    )
//
//                    value.firstOrEmptyString() !in setOf("true", "false") -> FieldTypeValidationResult(
//                        valid = false,
//                        message = "AllowAddValue must be either 'true' or 'false'"
//                    )
//
//                    else -> FieldTypeValidationResult(valid = true)
//                }
//            }
//        )
//    )

    override fun validationValues(values: Set<String>, attributes: Set<Attribute>): List<FieldTypeValidationResult> {
        val results = mutableListOf<FieldTypeValidationResult>()

        if (values.size != 1) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Dropdown field can has only 1 value"
                )
            )
        }

        val value = values.firstOrEmptyString()
        if (value.length > 50) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Dropdown field value cannot exceed 50 characters"
                )
            )
        }

        if (value.isEmpty()) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Dropdown field value cannot be empty"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        val allowAddValue = getAllowAddValue(attributes)
        val allowValues = getValues(attributes)

        if (value !in allowValues && !allowAddValue) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "The value is not in the list of allowed values, and adding new values is not permitted."
                )
            )
        }

        return results
    }


}