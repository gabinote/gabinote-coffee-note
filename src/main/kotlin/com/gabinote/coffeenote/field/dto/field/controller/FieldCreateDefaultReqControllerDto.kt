package com.gabinote.coffeenote.field.dto.field.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.field.util.validation.fieldType.RequiredFieldType
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FieldCreateDefaultReqControllerDto(

    @field:NotBlank(message = "name must not be blank")
    @field:Length(max = 50, message = "name must be at most 50 characters")
    val name: String,

    @field:NotBlank(message = "icon must not be blank")
    @field:Length(max = 50, message = "icon must be at most 50 characters")
    val icon: String,

    @field:RequiredFieldType
    val type: String,

    @field:Valid
    @field:Size(max = 5, message = "attributes must be at most 5")
    val attributes: Set<AttributeCreateReqControllerDto> = setOf(),
)