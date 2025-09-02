package com.gabinote.coffeenote.testSupport.testUtil.data.field

import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeResControllerDto
import com.gabinote.coffeenote.field.dto.field.controller.FieldCreateReqControllerDto
import com.gabinote.coffeenote.field.dto.field.controller.FieldResControllerDto
import java.util.*

object FieldTestDataHelper {
    fun createTestFieldResControllerDto(
        externalId: String = UUID.randomUUID().toString(),
        default: Boolean = false,
        name: String = "name",
        icon: String = "icon",
        type: String = "type",
        attributes: Set<AttributeResControllerDto> = setOf(
            AttributeResControllerDto(key = "test", value = setOf("test"))
        ),
        owner: String? = null,
    ): FieldResControllerDto {
        return FieldResControllerDto(
            externalId = externalId,
            default = default,
            name = name,
            icon = icon,
            type = type,
            attributes = attributes,
            owner = owner,
        )
    }

    fun createTestFieldCreateReqControllerDto(
        name: String = "name",
        icon: String = "icon",
        type: String = "type",
        attributes: Set<AttributeCreateReqControllerDto> = setOf(
            AttributeCreateReqControllerDto(key = "test", value = setOf("test"))
        ),
    ): FieldCreateReqControllerDto {
        return FieldCreateReqControllerDto(
            name = name,
            icon = icon,
            type = type,
            attributes = attributes,
        )

    }

}