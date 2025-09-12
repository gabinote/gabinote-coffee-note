package com.gabinote.coffeenote.template.dto.templateField.controller

import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeUpdateReqControllerDto

data class TemplateFieldPatchReqControllerDto(
    val id: String,
    val name: String? = null,
    val icon: String? = null,
    val type: String? = null,
    val order: Int? = null,

    @JvmField
    var isDisplay: Boolean? = null,
    var attributes: Set<AttributeUpdateReqControllerDto> = emptySet(),
)