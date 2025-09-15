package com.gabinote.coffeenote.template.dto.template.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldCreateReqServiceDto

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TemplateUpdateDefaultReqControllerDto(
    val name: String,
    val icon: String,
    val description: String,
    val fields: List<TemplateFieldCreateReqServiceDto> = emptyList(),
)