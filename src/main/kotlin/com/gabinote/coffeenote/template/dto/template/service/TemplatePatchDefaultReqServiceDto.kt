package com.gabinote.coffeenote.template.dto.template.service

import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldPatchReqServiceDto

data class TemplatePatchDefaultReqServiceDto(
    val name: String? = null,
    val icon: String? = null,
    val description: String? = null,
    val fields: List<TemplateFieldPatchReqServiceDto> = emptyList(),
)