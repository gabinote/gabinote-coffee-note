package com.gabinote.coffeenote.field.mapping.attribute

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeResControllerDto
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeUpdateReqControllerDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeResServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeUpdateReqServiceDto
import org.mapstruct.Mapper

@Mapper(
    componentModel = "spring",
)
interface AttributeMapper {


    fun toAttributeResServiceDto(
        attribute: Attribute
    ): AttributeResServiceDto

    fun toAttributeResControllerDto(
        dto: AttributeResServiceDto,
    ): AttributeResControllerDto

    fun toAttribute(
        dto: AttributeCreateReqServiceDto,
    ): Attribute

    fun toAttribute(
        dto: AttributeUpdateReqServiceDto,
    ): Attribute

    fun toAttributeCreateReqServiceDto(
        dto: AttributeCreateReqControllerDto,
    ): AttributeCreateReqServiceDto

    fun toAttributeUpdateReqServiceDto(
        dto: AttributeUpdateReqControllerDto,
    ): AttributeUpdateReqServiceDto
}