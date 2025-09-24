package com.gabinote.coffeenote.template.dto.templateField.service

import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeUpdateReqServiceDto

data class TemplateFieldPatchReqServiceDto(
    val id: String,
    val name: String? = null,
    val icon: String? = null,
    val type: FieldType? = null,
    val order: Int? = null,

    @JvmField
    var isDisplay: Boolean? = null,
    var attributes: Set<AttributeUpdateReqServiceDto> = emptySet(),
)