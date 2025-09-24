package com.gabinote.coffeenote.template.dto.template.service

import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldResServiceDto
import org.bson.types.ObjectId
import java.util.*

data class TemplateResServiceDto(
    val id: ObjectId,
    val externalId: UUID,
    val name: String,
    val icon: String,
    val description: String,
    @JvmField
    val isOpen: Boolean,
    val owner: String?,
    @JvmField
    val isDefault: Boolean,
    val fields: List<TemplateFieldResServiceDto> = emptyList(),
)