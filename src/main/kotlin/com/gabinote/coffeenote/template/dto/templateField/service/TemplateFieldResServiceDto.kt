package com.gabinote.coffeenote.template.dto.templateField.service

import com.gabinote.coffeenote.field.dto.attribute.service.AttributeResServiceDto

data class TemplateFieldResServiceDto(
    val id: String,
    val name: String,
    val icon: String,
    val type: String,
    val order: Int,
    var isDisplay: Boolean,
    var attributes: Set<AttributeResServiceDto> = emptySet(),
)