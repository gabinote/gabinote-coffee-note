package com.gabinote.coffeenote.field.dto.field.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.common.dto.attribute.controller.AttributeUpdateReqControllerDto

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FieldUpdateDefaultReqControllerDto(
    val name: String?,
    val icon: String?,
    val attributes: Set<AttributeUpdateReqControllerDto>,
)