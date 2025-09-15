package com.gabinote.coffeenote.template.dto.template.service

import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldPatchReqServiceDto

data class TemplatePatchReqServiceDto(
    val name: String? = null,
    val icon: String? = null,
    val description: String? = null,
    @JvmField
    val isOpen: Boolean? = null,
    val owner: String,
    val fields: List<TemplateFieldPatchReqServiceDto> = emptyList(),
)