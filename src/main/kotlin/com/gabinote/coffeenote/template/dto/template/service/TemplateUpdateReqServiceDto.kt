package com.gabinote.coffeenote.template.dto.template.service

import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldCreateReqServiceDto
import java.util.*

data class TemplateUpdateReqServiceDto(
    val externalId: UUID,
    val name: String,
    val icon: String,
    val description: String,
    @JvmField
    val isOpen: Boolean,
    val owner: String,
    val fields: List<TemplateFieldCreateReqServiceDto> = emptyList(),
)