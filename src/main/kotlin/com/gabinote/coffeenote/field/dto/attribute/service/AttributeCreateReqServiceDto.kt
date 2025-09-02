package com.gabinote.coffeenote.field.dto.attribute.service

data class AttributeCreateReqServiceDto(
    val key: String,
    val value: Set<String>
)