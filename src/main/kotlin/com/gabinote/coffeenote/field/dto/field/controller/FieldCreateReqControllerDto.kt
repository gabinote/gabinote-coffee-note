package com.gabinote.coffeenote.field.dto.field.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.common.dto.attribute.controller.AttributeCreateReqControllerDto

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FieldCreateReqControllerDto(
    val name: String,
    val icon: String,
    val type: String,
    val attributes: Set<AttributeCreateReqControllerDto>,
)