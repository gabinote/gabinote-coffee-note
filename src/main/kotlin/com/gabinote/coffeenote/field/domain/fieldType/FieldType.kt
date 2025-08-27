package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.common.dto.attribute.service.AttributeServiceDto

abstract class FieldType {
    abstract val key : String
    abstract val fieldTypeAttributeKeys : Set<FieldTypeAttributeKey>
    abstract fun valueValidation(values:Set<String>): List<FieldTypeValidationResult>

    fun validationKey(attributes: Set<AttributeServiceDto>): List<FieldTypeValidationResult> {
        val attributeMap = attributes.associateBy { it.key }
        return fieldTypeAttributeKeys.map{ fieldTypeAttributeKey ->
            validateAttribute(fieldTypeAttributeKey, attributeMap)
        }
    }



    private fun validateAttribute(
        fieldTypeAttributeKey: FieldTypeAttributeKey,
        attributeMap: Map<String, AttributeServiceDto>
    ): FieldTypeValidationResult {
        return attributeMap[fieldTypeAttributeKey.key]?.let { value ->
            fieldTypeAttributeKey.validationFunc(value.value)
        } ?: FieldTypeValidationResult(
            valid = false,
            message = "Unknown attribute key: ${fieldTypeAttributeKey.key}"
        )
    }
}