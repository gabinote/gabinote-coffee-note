package com.gabinote.coffeenote.testSupport.testUtil.data.template

import com.gabinote.coffeenote.template.dto.template.controller.TemplateCreateReqControllerDto
import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldCreateReqControllerDto
import com.gabinote.coffeenote.testSupport.testUtil.data.template.TemplateFieldDataHelper.createTestTemplateFieldCreateReqControllerDto

object TemplateTestDataHelper {

    fun createTemplateCreateReqControllerDto(
        name: String = "test",
        icon: String = "test",
        description: String = "test",
        isOpen: Boolean = false,
        fields: List<TemplateFieldCreateReqControllerDto> = listOf(
            createTestTemplateFieldCreateReqControllerDto()
        ),
    ): TemplateCreateReqControllerDto {

        return TemplateCreateReqControllerDto(
            name = name,
            icon = icon,
            description = description,
            isOpen = isOpen,
            fields = fields,
        )

    }

}