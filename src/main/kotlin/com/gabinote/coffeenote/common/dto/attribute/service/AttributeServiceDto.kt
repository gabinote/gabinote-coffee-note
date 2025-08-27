package com.gabinote.coffeenote.common.dto.attribute.service

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AttributeServiceDto(
    val key: String,
    val value: Set<String>
)