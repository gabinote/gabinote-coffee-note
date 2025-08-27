package com.gabinote.coffeenote.common.mapping.attribute

import com.gabinote.coffeenote.common.dto.attribute.controller.AttributeControllerDto
import com.gabinote.coffeenote.common.dto.attribute.service.AttributeServiceDto
import org.springframework.stereotype.Component
import org.mapstruct.*
@Mapper(
    componentModel = "spring",
)
interface AttributeMapper {

    fun toAttributeServiceDto(
        dto: AttributeControllerDto,
    ): AttributeServiceDto

    fun toAttributeControllerDto(
        dto: AttributeServiceDto,
    ): AttributeControllerDto

}