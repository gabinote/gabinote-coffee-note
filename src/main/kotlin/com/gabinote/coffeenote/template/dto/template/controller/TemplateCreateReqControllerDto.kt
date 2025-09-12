package com.gabinote.coffeenote.template.dto.template.controller

import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldCreateReqControllerDto

data class TemplateCreateReqControllerDto(
    val name: String,
    val icon: String,
    val description: String,
    val isOpen: Boolean,
    val fields: List<TemplateFieldCreateReqControllerDto> = emptyList(),
)