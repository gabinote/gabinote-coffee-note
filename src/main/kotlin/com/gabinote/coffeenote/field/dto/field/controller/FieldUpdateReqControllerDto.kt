package com.gabinote.coffeenote.field.dto.field.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.common.util.validation.string.blank.NullableNotBlank
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeUpdateReqControllerDto
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FieldUpdateReqControllerDto(

    @field:NullableNotBlank(resourceName = "name")
    @field:Length(max = 50, message = "name must be at most 50 characters")
    val name: String? = null,

    @field:NullableNotBlank(resourceName = "icon")
    @field:Length(max = 50, message = "icon must be at most 50 characters")
    val icon: String? = null,

    @field:Valid
    @field:Size(max = 5, message = "attributes must be at most 5")
    val attributes: Set<AttributeUpdateReqControllerDto> = emptySet(),
)