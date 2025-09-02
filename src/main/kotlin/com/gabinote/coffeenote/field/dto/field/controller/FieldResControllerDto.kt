package com.gabinote.coffeenote.field.dto.field.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeResControllerDto

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FieldResControllerDto(
    val externalId: String,
    val default: Boolean,
    val name: String,
    val icon: String,
    val type: String,
    val attributes: Set<AttributeResControllerDto>,
    val owner: String?,
)