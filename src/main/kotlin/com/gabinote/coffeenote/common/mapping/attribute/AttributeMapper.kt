package com.gabinote.coffeenote.common.mapping.attribute

import com.gabinote.coffeenote.common.domain.attribute.Attribute
import com.gabinote.coffeenote.common.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.common.dto.attribute.controller.AttributeResControllerDto
import com.gabinote.coffeenote.common.dto.attribute.controller.AttributeUpdateReqControllerDto
import com.gabinote.coffeenote.common.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.common.dto.attribute.service.AttributeResServiceDto
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

    fun toAttributeCreateReqServiceDto(
        dto: AttributeCreateReqControllerDto,
    ): AttributeCreateReqServiceDto

    fun toAttributeUpdateReqServiceDto(
        dto: AttributeUpdateReqControllerDto,
    ): AttributeCreateReqServiceDto
}