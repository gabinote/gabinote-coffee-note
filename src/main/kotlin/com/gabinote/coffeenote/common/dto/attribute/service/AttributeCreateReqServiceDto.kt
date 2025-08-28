package com.gabinote.coffeenote.common.dto.attribute.service

data class AttributeCreateReqServiceDto(
    val key: String,
    val value: Set<String>
)