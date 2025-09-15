package com.gabinote.coffeenote.testSupport.testUtil.data.template

import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldCreateReqControllerDto
import com.gabinote.coffeenote.testSupport.testUtil.data.field.AttributeTestDataHelper.createTestAttributeCreateReqControllerDto

object TemplateFieldDataHelper {

    fun createTestTemplateFieldCreateReqControllerDto(
        id: String = "d6283550-a30a-455e-a522-8ba486a1ae7f",
        name: String = "test",
        icon: String = "test",
        type: String = "test",
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

}