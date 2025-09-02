package com.gabinote.coffeenote.field.dto.attribute.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.common.util.validation.collection.CollectionElementLength
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AttributeCreateReqControllerDto(

    @field:NotBlank(message = "key must not be blank")
    @field:Length(max = 50, message = "key must be at most 50 characters")
    val key: String,

    @field:CollectionElementLength(length = 50, resourceName = "attribute value")
    @field:Size(max = 100, message = "value must be at most 100")
    val value: Set<String> = setOf()
)