package com.gabinote.coffeenote.field.dto.field.service

import com.gabinote.coffeenote.common.dto.attribute.service.AttributeUpdateReqServiceDto

data class FieldUpdateDefaultReqServiceDto(
    val externalId: String,
    val name: String?,
    val icon: String?,
    val attributes: Set<AttributeUpdateReqServiceDto> = setOf(),
)