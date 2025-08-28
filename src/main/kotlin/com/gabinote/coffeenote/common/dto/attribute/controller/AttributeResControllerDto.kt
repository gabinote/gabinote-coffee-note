package com.gabinote.coffeenote.common.dto.attribute.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AttributeResControllerDto(
    val key: String,
    val value: Set<String>
)