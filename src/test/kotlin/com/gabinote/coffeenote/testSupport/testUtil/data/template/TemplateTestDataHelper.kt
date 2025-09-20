package com.gabinote.coffeenote.testSupport.testUtil.data.template

import com.gabinote.coffeenote.template.domain.template.Template
import com.gabinote.coffeenote.template.domain.templateField.TemplateField
import com.gabinote.coffeenote.template.dto.template.controller.TemplateCreateReqControllerDto
import com.gabinote.coffeenote.template.dto.template.controller.TemplateResControllerDto
import com.gabinote.coffeenote.template.dto.template.service.TemplateResServiceDto
import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldCreateReqControllerDto
import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldResServiceDto
import com.gabinote.coffeenote.testSupport.testUtil.data.template.TemplateFieldDataHelper.createTestTemplateField
import com.gabinote.coffeenote.testSupport.testUtil.data.template.TemplateFieldDataHelper.createTestTemplateFieldControllerDto
import com.gabinote.coffeenote.testSupport.testUtil.data.template.TemplateFieldDataHelper.createTestTemplateFieldCreateReqControllerDto
import com.gabinote.coffeenote.testSupport.testUtil.data.template.TemplateFieldDataHelper.createTestTemplateFieldServiceDto
import org.bson.types.ObjectId
import java.util.*

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

    fun createTestTemplate(
        externalId: UUID = UUID.fromString("d6283550-a30a-455e-a522-8ba486a1ae7f"),
        name: String = "test",
        icon: String = "test",
        description: String = "test",
        isDefault: Boolean = false,
        isOpen: Boolean = false,
        owner: String = "test",
        fields: List<TemplateField> = listOf(createTestTemplateField())
    ) = Template(
        id = null,
        externalId = externalId.toString(),
        name = name,
        icon = icon,
        description = description,
        isDefault = isDefault,
        isOpen = isOpen,
        owner = owner,
        fields = fields
    )

    fun createTestTemplateResServiceDto(
        externalId: UUID = UUID.fromString("d6283550-a30a-455e-a522-8ba486a1ae7f"),
        name: String = "test",
        icon: String = "test",
        description: String = "test",
        isDefault: Boolean = false,
        isOpen: Boolean = false,
        owner: String = "test",
        fields: List<TemplateFieldResServiceDto> = listOf(createTestTemplateFieldServiceDto())
    ) = TemplateResServiceDto(
        id = ObjectId(),
        externalId = externalId,
        name = name,
        icon = icon,
        description = description,
        isDefault = isDefault,
        isOpen = isOpen,
        owner = owner,
        fields = fields
    )

    fun createTestTemplateResControllerDto(
        externalId: UUID = UUID.randomUUID(),
        name: String = "Test Template",
        icon: String = "test-icon",
        description: String = "Test Description",
        isOpen: Boolean = false,
        owner: String? = null,
        isDefault: Boolean = true
    ) = TemplateResControllerDto(
        externalId = externalId,
        name = name,
        icon = icon,
        description = description,
        isOpen = isOpen,
        owner = owner,
        isDefault = isDefault,
        fields = listOf(createTestTemplateFieldControllerDto())
    )

}