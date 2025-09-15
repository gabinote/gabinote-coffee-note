package com.gabinote.coffeenote.template.dto.template.controller

import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldResControllerDto

data class TemplateResControllerDto(
    val externalId: String,
    val name: String,
    val icon: String,
    val description: String,
    @JvmField
    val isOpen: Boolean,
    val owner: String?,
    @JvmField
    val isDefault: Boolean,
    val fields: List<TemplateFieldResControllerDto> = emptyList(),
)