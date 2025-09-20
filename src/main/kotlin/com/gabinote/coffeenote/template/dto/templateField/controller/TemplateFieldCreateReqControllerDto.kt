package com.gabinote.coffeenote.template.dto.templateField.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.common.util.regex.RegexHelper
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.template.dto.template.constraint.TemplateFieldConstraints
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TemplateFieldCreateReqControllerDto(

    @field:NotBlank(message = "Name cannot be blank")
    @field:Pattern(
        regexp = RegexHelper.UUID,
        message = "id must be a valid UUID"
    )
    val id: String,

    @field:NotBlank(message = "Field name cannot be blank")
    @field:Length(
        max = TemplateFieldConstraints.NAME_MAX_LENGTH,
        message = "Field name must be at most ${TemplateFieldConstraints.NAME_MAX_LENGTH} characters"
    )
    val name: String,

    @field:NotBlank(message = "Field icon cannot be blank")
    @field:Length(
        max = TemplateFieldConstraints.ICON_MAX_LENGTH,
        message = "Field icon must be at most ${TemplateFieldConstraints.ICON_MAX_LENGTH} characters"
    )
    val icon: String,

    val type: FieldType,

    val order: Int,

    @JvmField
    var isDisplay: Boolean,

    @field:Valid
    @field:Size(
        max = 5,
        message = "attributes must be at most 5"
    )
    var attributes: Set<AttributeCreateReqControllerDto> = emptySet(),
)