package com.gabinote.coffeenote.field.dto.field.service

import com.gabinote.coffeenote.field.dto.attribute.service.AttributeUpdateReqServiceDto
import java.util.*

data class FieldUpdateDefaultReqServiceDto(
    val externalId: UUID,
    val name: String?,
    val icon: String?,
    val attributes: Set<AttributeUpdateReqServiceDto> = setOf(),
)