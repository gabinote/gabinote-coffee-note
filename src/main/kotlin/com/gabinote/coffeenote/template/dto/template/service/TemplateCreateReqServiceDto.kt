package com.gabinote.coffeenote.template.dto.template.service

import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldCreateReqServiceDto

data class TemplateCreateReqServiceDto(
    val name: String,
    val icon: String,
    val description: String,
    val isOpen: Boolean,
    val owner: String,
    val fields: List<TemplateFieldCreateReqServiceDto> = emptyList(),
)