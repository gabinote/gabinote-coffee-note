package com.gabinote.coffeenote.template.dto.template.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.template.dto.template.constraint.TemplateConstraints
import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldCreateReqControllerDto
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TemplateUpdateDefaultReqControllerDto(
    @field:NotBlank(message = "name must not be blank")
    @field:Length(
        max = TemplateConstraints.NAME_MAX_LENGTH,
        message = "name must be at most ${TemplateConstraints.NAME_MAX_LENGTH} characters"
    )
    val name: String,

    @field:NotBlank(message = "icon must not be blank")
    @field:Length(
        max = TemplateConstraints.FIELD_MAX_COUNT,
        message = "icon must be at most ${TemplateConstraints.FIELD_MAX_COUNT} characters"
    )
    val icon: String,

    @field:NotBlank(message = "description must not be blank")
    @field:Length(
        max = TemplateConstraints.FIELD_MAX_COUNT,
        message = "description must be at most ${TemplateConstraints.FIELD_MAX_COUNT} characters"
    )
    val description: String,

    @field:Valid
    @field:Size(
        max = TemplateConstraints.FIELD_MAX_COUNT,
        message = "fields must be at most ${TemplateConstraints.FIELD_MAX_COUNT}"
    )
    val fields: List<TemplateFieldCreateReqControllerDto> = emptyList(),
)