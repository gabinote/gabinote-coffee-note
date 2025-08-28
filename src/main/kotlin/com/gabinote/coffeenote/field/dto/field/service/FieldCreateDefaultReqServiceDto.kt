package com.gabinote.coffeenote.field.dto.field.service

import com.gabinote.coffeenote.common.dto.attribute.service.AttributeCreateReqServiceDto

data class FieldCreateDefaultReqServiceDto(
    val name: String,
    val icon: String,
    val type: String,
    val attributes: Set<AttributeCreateReqServiceDto>,
)