package com.gabinote.coffeenote.note.dto.noteField.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeResControllerDto

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NoteFieldResControllerDto(
    var id: String,

    var name: String,

    var icon: String,

    var type: FieldType,

    var attributes: Set<AttributeResControllerDto> = emptySet(),

    var order: Int = 0,

    @JvmField
    var isDisplay: Boolean = true,

    var values: Set<String> = emptySet(),
)