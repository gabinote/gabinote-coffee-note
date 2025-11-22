package com.gabinote.coffeenote.note.dto.noteField.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.common.util.regex.RegexHelper
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NoteFieldCreateReqControllerDto(

    @field:NotBlank(message = "Id cannot be blank")
    @field:Pattern(
        regexp = RegexHelper.UUID,
        message = "id must be a valid UUID"
    )
    var id: String,

    var name: String,

    var icon: String,

    var type: FieldType,

    var attributes: Set<AttributeCreateReqServiceDto> = emptySet(),

    var order: Int = 0,

    @JvmField
    var isDisplay: Boolean = true,

    var values: Set<String> = emptySet(),
)