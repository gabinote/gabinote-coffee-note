package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.common.util.type.TypeCheckHelper.isInt
import com.gabinote.coffeenote.field.domain.attribute.Attribute

object ScoreField : FieldType() {
    override val key: String
        get() = "SCORE"

    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf(
        FieldTypeAttributeKey(
            key = "maxScore",
            validationFunc = { value ->

                val valueIsInt = isInt(value.firstOrEmptyString())

                when {
                    value.size != 1 -> FieldTypeValidationResult(
                        valid = false,
                        message = "maxScore must have exactly 1 value"
                    )

                    !valueIsInt -> FieldTypeValidationResult(
                        valid = false,
                        message = "maxScore value must be an integer"
                    )

                    valueIsInt && value.firstOrEmptyString().toInt() > 10 -> FieldTypeValidationResult(
                        valid = false,
                        message = "maxScore value cannot be greater than 10"
                    )

                    valueIsInt && value.firstOrEmptyString().toInt() < 3 -> FieldTypeValidationResult(
                        valid = false,
                        message = "maxScore value cannot be less than 3"
                    )

                    else -> FieldTypeValidationResult(valid = true)
                }
            }
        ),
    )

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
        val valueIsInt = isInt(value)

        if (!valueIsInt) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Date field value must be an integer"
                )
            )
        }
        val maxScore = getMaxScore(attributes)
        if (valueIsInt && value.toInt() > maxScore) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Date field value cannot be greater than $maxScore"
                )
            )
        }

        if (valueIsInt && value.toInt() < 1) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Date field value cannot be less than 1"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

    private fun getMaxScore(attributes: Set<Attribute>): Int {
        val source = attributes.firstOrNull { it.key == "maxScore" }?.value

        if (source == null || source.size != 1) {
            throw IllegalArgumentException("Invalid maxScore attribute")
        }

        if (!isInt(source.first())) {
            throw IllegalArgumentException("Invalid maxScore attribute value")
        }

        return source.first().toInt()

    }
}