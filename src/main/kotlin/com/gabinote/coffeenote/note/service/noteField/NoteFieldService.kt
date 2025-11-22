package com.gabinote.coffeenote.note.service.noteField

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotValid
import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.service.attribute.AttributeService
import com.gabinote.coffeenote.field.util.validation.fieldType.FieldTypeValidationResultHelper.failures
import com.gabinote.coffeenote.field.util.validation.fieldType.FieldTypeValidationResultHelper.isValid
import com.gabinote.coffeenote.note.domain.note.NoteField
import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldCreateReqServiceDto
import com.gabinote.coffeenote.note.mapping.noteField.NoteFieldMapper
import com.gabinote.coffeenote.policy.domain.policy.PolicyKey
import com.gabinote.coffeenote.policy.service.policy.PolicyService
import org.springframework.stereotype.Service

@Service
class NoteFieldService(
    private val attributeService: AttributeService,
    private val noteFieldMapper: NoteFieldMapper,
    private val policyService: PolicyService,
) {

    fun create(dto: List<NoteFieldCreateReqServiceDto>): List<NoteField> {
        validationNoteField(dto)
        val fields = dto.map { create(it) }
        return fields.sortedBy { it.order }
    }


    fun create(dto: NoteFieldCreateReqServiceDto): NoteField {
        val attributes = attributeService.createAttribute(
            fieldType = dto.type,
            attributesCreateReq = dto.attributes
        )

        validationValues(
            fieldType = dto.type,
            values = dto.values,
            attributes = attributes
        )

        val data = noteFieldMapper.toNoteField(
            dto = dto
        ).apply {
            changeAttributes(attributes)
        }



        return data
    }


    private fun validationNoteField(dto: List<NoteFieldCreateReqServiceDto>) {
        checkOrdersValid(dto)
        checkDisplayedFieldCount(dto)
    }

    private fun checkDisplayedFieldCount(dto: List<NoteFieldCreateReqServiceDto>) {
        val displayFieldCount = dto.count { it.isDisplay }
        val maxDisplayFieldCount = policyService.getByKey(PolicyKey.NOTE_MAX_DISPLAYED_FIELD_COUNT).toInt()
        if (displayFieldCount > maxDisplayFieldCount) {
            throw ResourceNotValid(
                name = "NoteField",
                reasons = listOf("The number of display fields exceeds the maximum allowed count of $maxDisplayFieldCount.")
            )
        }
    }

    private fun checkOrdersValid(dto: List<NoteFieldCreateReqServiceDto>) {
        val orders = dto.map { it.order }
        val orderSet = orders.toSet()
        if (orders.size != orderSet.size) {
            throw ResourceNotValid(
                name = "NoteField",
                reasons = listOf("Duplicate order values are not allowed.")
            )
        }

        val expectedOrders = (1..orders.size).toSet()
        if (orderSet != expectedOrders) {
            throw ResourceNotValid(
                name = "NoteField",
                reasons = listOf("Order values must be a continuous sequence starting from 1 to ${orders.size}.")
            )
        }
    }


    fun validationValues(fieldType: FieldType, values: Set<String>, attributes: Set<Attribute>) {
        val validationRes = fieldType.validationValues(
            values = values,
            attributes = attributes
        )

        if (!validationRes.isValid()) {
            throw ResourceNotValid(
                name = "Note Field Values",
                reasons = validationRes.failures()
            )
        }
    }
}