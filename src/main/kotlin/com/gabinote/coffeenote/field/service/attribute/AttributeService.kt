package com.gabinote.coffeenote.field.service.attribute

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotValid
import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeUpdateReqServiceDto
import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapper
import org.springframework.stereotype.Service

@Service
class AttributeService(
    private val attributeMapper: AttributeMapper,
) {

    fun createAttribute(fieldType: FieldType, attributesCreateReq: Set<AttributeCreateReqServiceDto>): Set<Attribute> {
        val attributes = attributesCreateReq.map { attributeMapper.toAttribute(it) }.toSet()
        checkAttributesValid(fieldType = fieldType, attributes = attributes)

        return attributes
    }

    fun updateAttribute(
        fieldType: FieldType,
        oldAttributes: Set<Attribute>,
        newAttributeReq: Set<AttributeUpdateReqServiceDto>
    ): Set<Attribute> {

        val newAttributes = newAttributeReq.map { attributeMapper.toAttribute(it) }.toSet()

        // 변경된 속성이 없을 경우 기존 속성 반환
        if (newAttributes.isEmpty() || oldAttributes == newAttributes) {
            return oldAttributes
        }

        updateFromNewAttribute(existsAttribute = oldAttributes, newAttributes = newAttributes)
        checkAttributesValid(fieldType = fieldType, attributes = oldAttributes)
        return oldAttributes
    }

    private fun updateFromNewAttribute(existsAttribute: Set<Attribute>, newAttributes: Set<Attribute>) {
        val errors = mutableListOf<String>()
        var raisedError = false
        val existsMap = existsAttribute.associateBy { it.key }


        for (newAttribute in newAttributes) {

            val target = existsMap[newAttribute.key]

            if (target == null) {
                errors.add("Unknown attribute key: ${newAttribute.key}")
                raisedError = true
                continue
            }

            if (raisedError) {
                continue
            }

            target.changeValue(newAttribute.value)
        }
        if (errors.isNotEmpty()) {
            throw ResourceNotValid(name = "Attribute", reasons = errors)
        }
    }

    private fun checkAttributesValid(fieldType: FieldType, attributes: Set<Attribute>) {
        val validationRes = fieldType.validationAttributes(attributes)

        if (validationRes.any { !it.valid }) {
            val errors = validationRes.filter { !it.valid }.map { it.message!! }
            throw ResourceNotValid(name = "Attribute", reasons = errors)
        }
    }

}
