package com.gabinote.coffeenote.field.dto.field.service

import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto

data class FieldCreateReqServiceDto(
    val default: Boolean,
    val name: String,
    val icon: String,
    val type: String,
    val attributes: Set<AttributeCreateReqServiceDto>,
    val owner: String,
)