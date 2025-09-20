package com.gabinote.coffeenote.template.dto.templateField.service

import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeResServiceDto

data class TemplateFieldResServiceDto(
    val id: String,
    val name: String,
    val icon: String,
    val type: FieldType,
    val order: Int,

    @JvmField
    var isDisplay: Boolean,
    var attributes: Set<AttributeResServiceDto> = emptySet(),
)