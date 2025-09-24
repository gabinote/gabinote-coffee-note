package com.gabinote.coffeenote.testSupport.testUtil.data.field

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.field.Field
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeResControllerDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeUpdateReqServiceDto
import com.gabinote.coffeenote.field.dto.field.controller.FieldCreateReqControllerDto
import com.gabinote.coffeenote.field.dto.field.controller.FieldResControllerDto
import com.gabinote.coffeenote.field.dto.field.service.FieldCreateDefaultReqServiceDto
import com.gabinote.coffeenote.field.dto.field.service.FieldUpdateDefaultReqServiceDto
import org.bson.types.ObjectId
import java.util.*

//TODO: 변경된 스펙에 맞게 수정 필요
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
            isDefault = default,
            name = name,
            icon = icon,
            type = TestFieldType,
            attributes = attributes,
            owner = owner,
        )
    }

    fun createTestField(
        id: ObjectId = ObjectId(),
        externalId: String = UUID.randomUUID().toString(),
        default: Boolean = false,
        name: String = "name",
        icon: String = "icon",
        type: String = "type",
        attributes: Set<Attribute> = setOf(
            Attribute(key = "test", value = setOf("test"))
        ),
        owner: String? = null,
    ): Field {
        return Field(
            id = id,
            externalId = externalId,
            isDefault = default,
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
            type = TestFieldType,
            attributes = attributes,
        )

    }

    fun createTestFieldCreateDefaultReqServiceDto(
        name: String = "name",
        icon: String = "icon",
        type: String = "type",
        attributes: Set<AttributeCreateReqServiceDto> = setOf(
            AttributeCreateReqServiceDto(
                key = "test",
                value = setOf("test")
            )
        ),
    ): FieldCreateDefaultReqServiceDto {
        return FieldCreateDefaultReqServiceDto(
            name = name,
            icon = icon,
            type = TestFieldType,
            attributes = attributes,
        )

    }

    fun createTestFieldUpdateReqServiceDto(
        externalId: UUID = UUID.randomUUID(),
        name: String = "updatedName",
        icon: String = "updatedIcon",
        attributes: Set<AttributeUpdateReqServiceDto> = setOf(
            AttributeUpdateReqServiceDto(
                key = "test",
                value = setOf("test")
            )
        ),
    ): FieldUpdateDefaultReqServiceDto {
        return FieldUpdateDefaultReqServiceDto(
            externalId = externalId,
            name = name,
            icon = icon,
            attributes = attributes,
        )

    }

}