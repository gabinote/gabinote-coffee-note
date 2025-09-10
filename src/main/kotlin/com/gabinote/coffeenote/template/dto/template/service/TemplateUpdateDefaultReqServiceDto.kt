package com.gabinote.coffeenote.template.dto.template.service

import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldCreateReqServiceDto

data class TemplateUpdateDefaultReqServiceDto(
    val name: String,
    val icon: String,
    val description: String,
    val fields: List<TemplateFieldCreateReqServiceDto> = emptyList(),
)