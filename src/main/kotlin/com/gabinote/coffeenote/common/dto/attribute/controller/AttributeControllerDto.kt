package com.gabinote.coffeenote.common.dto.attribute.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AttributeControllerDto(
    val key: String,
    val value: Set<String>
)