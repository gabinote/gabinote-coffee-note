package com.gabinote.coffeenote.template.service.templateField

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotValid
import com.gabinote.coffeenote.field.service.attribute.AttributeService
import com.gabinote.coffeenote.template.domain.templateField.TemplateField
import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldCreateReqServiceDto
import com.gabinote.coffeenote.template.mapping.templateField.TemplateFieldMapper
import org.springframework.stereotype.Service


@Service
class TemplateFieldService(
    private val attributeService: AttributeService,
    private val templateFieldMapper: TemplateFieldMapper
) {
    fun create(dto: List<TemplateFieldCreateReqServiceDto>): List<TemplateField> {
        checkOrdersValid(dto)
        checkIdDuplicate(dto)
        checkDisplayValid(dto)
        val fields = dto.map { createTemplateField(dto = it) }
        return fields.sortedBy { it.order }
    }

    private fun createTemplateField(dto: TemplateFieldCreateReqServiceDto): TemplateField {
        val templateField = templateFieldMapper.toTemplateField(dto)
        val attributes = attributeService.createAttribute(fieldType = dto.type, attributesCreateReq = dto.attributes)
        templateField.changeAttributes(attributes)

        return templateField
    }

    private fun checkIdDuplicate(dto: List<TemplateFieldCreateReqServiceDto>) {
        val ids = dto.map { it.id }
        val idSet = ids.toSet()
        if (ids.size != idSet.size) {
            throw ResourceNotValid(
                name = "TemplateField",
                reasons = listOf("Duplicate IDs are not allowed.")
            )
        }
    }

    private fun checkOrdersValid(dto: List<TemplateFieldCreateReqServiceDto>) {
        val orders = dto.map { it.order }
        val orderSet = orders.toSet()
        if (orders.size != orderSet.size) {
            throw ResourceNotValid(
                name = "TemplateField",
                reasons = listOf("Duplicate order values are not allowed.")
            )
        }

        val expectedOrders = (1..orders.size).toSet()
        if (orderSet != expectedOrders) {
            throw ResourceNotValid(
                name = "TemplateField",
                reasons = listOf("Order values must be a continuous sequence starting from 1 to ${orders.size}.")
            )
        }
    }

    private fun checkDisplayValid(dto: List<TemplateFieldCreateReqServiceDto>) {
        dto.count { it.isDisplay }
//        if (displayCount == 0) {
//            throw ResourceNotValid(
//                name = "TemplateField",
//                reasons = listOf("At least one field must be set to display.")
//            )
//        }
        //TODO : 나중에 설정값 통해서 최대 표시 개수 제한 걸기

    }


}