package com.gabinote.coffeenote.template.dto.template.service

import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldCreateReqServiceDto
import java.util.*

data class TemplateUpdateDefaultReqServiceDto(
    val externalId: UUID,
    val name: String,
    val icon: String,
    val description: String,
    val fields: List<TemplateFieldCreateReqServiceDto> = emptyList(),
)