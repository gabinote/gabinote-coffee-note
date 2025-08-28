package com.gabinote.coffeenote.common.dto.attribute.service

data class AttributeUpdateReqServiceDto(
    val key: String,
    val value: Set<String>
)