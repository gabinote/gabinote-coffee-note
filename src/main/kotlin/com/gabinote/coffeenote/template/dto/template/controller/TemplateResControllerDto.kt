package com.gabinote.coffeenote.template.dto.template.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldResControllerDto
import java.util.*

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TemplateResControllerDto(
    val externalId: UUID,
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