package com.gabinote.coffeenote.field.dto.field.service

import com.gabinote.coffeenote.field.dto.attribute.service.AttributeUpdateReqServiceDto
import java.util.*

data class FieldUpdateReqServiceDto(
    val externalId: UUID,
    val name: String?,
    val icon: String?,
    val attributes: Set<AttributeUpdateReqServiceDto> = emptySet(),
    val owner: String,
)