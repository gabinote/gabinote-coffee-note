package com.gabinote.coffeenote.field.dto.attribute.service

data class AttributeUpdateReqServiceDto(
    val key: String,
    val value: Set<String>
)