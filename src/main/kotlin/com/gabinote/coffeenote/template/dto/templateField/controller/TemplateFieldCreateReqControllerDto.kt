package com.gabinote.coffeenote.template.dto.templateField.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeCreateReqControllerDto

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TemplateFieldCreateReqControllerDto(
    val id: String,
    val name: String,
    val icon: String,
    val type: String,
    val order: Int,
    @JvmField
    var isDisplay: Boolean,
    var attributes: Set<AttributeCreateReqControllerDto> = emptySet(),
)