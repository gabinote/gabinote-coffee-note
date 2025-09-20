package com.gabinote.coffeenote.testSupport.testUtil.data.template

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeResControllerDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeResServiceDto
import com.gabinote.coffeenote.template.domain.templateField.TemplateField
import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldCreateReqControllerDto
import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldResControllerDto
import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldResServiceDto
import com.gabinote.coffeenote.testSupport.testUtil.data.field.AttributeTestDataHelper
import com.gabinote.coffeenote.testSupport.testUtil.data.field.AttributeTestDataHelper.createTestAttributeCreateReqControllerDto
import com.gabinote.coffeenote.testSupport.testUtil.data.field.AttributeTestDataHelper.createTestAttributeResServiceDto
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldType

object TemplateFieldDataHelper {

    fun createTestTemplateFieldCreateReqControllerDto(
        id: String = "d6283550-a30a-455e-a522-8ba486a1ae7f",
        name: String = "test",
        icon: String = "test",
        type: FieldType = TestFieldType,
        order: Int = 0,
        isDisplay: Boolean = true,
        attribute: Set<AttributeCreateReqControllerDto> = setOf(
            createTestAttributeCreateReqControllerDto()
        )
    ) = TemplateFieldCreateReqControllerDto(
        id = id,
        name = name,
        icon = icon,
        type = type,
        order = order,
        isDisplay = isDisplay,
    )

    fun createTestTemplateField(
        id: String = "d6283550-a30a-455e-a522-8ba486a1ae7f",
        name: String = "test",
        icon: String = "test",
        type: String = "test",
        order: Int = 0,
        isDisplay: Boolean = true,
        attribute: Set<Attribute> = setOf(
            AttributeTestDataHelper.createTestAttribute()
        )
    ) = TemplateField(
        id = id,
        name = name,
        icon = icon,
        type = type,
        order = order,
        isDisplay = isDisplay,
        attributes = attribute.toSet()
    )

    fun createTestTemplateFieldServiceDto(
        id: String = "d6283550-a30a-455e-a522-8ba486a1ae7f",
        name: String = "test",
        icon: String = "test",
        type: FieldType = TestFieldType,
        order: Int = 0,
        isDisplay: Boolean = true,
        attributes: Set<AttributeResServiceDto> = setOf(
            createTestAttributeResServiceDto(),
        )
    ) = TemplateFieldResServiceDto(
        id = id,
        name = name,
        icon = icon,
        type = type,
        order = order,
        isDisplay = isDisplay,
        attributes = attributes.toSet()
    )

    fun createTestTemplateFieldControllerDto(
        id: String = "d6283550-a30a-455e-a522-8ba486a1ae7f",
        name: String = "test",
        icon: String = "test",
        type: FieldType = TestFieldType,
        order: Int = 0,
        isDisplay: Boolean = true,
        attributes: Set<AttributeResControllerDto> = setOf(
            AttributeResControllerDto(
                key = "test",
                value = setOf("test")
            )
        )
    ) = TemplateFieldResControllerDto(
        id = id,
        name = name,
        icon = icon,
        type = type,
        order = order,
        isDisplay = isDisplay,
        attributes = attributes.toSet()
    )

}