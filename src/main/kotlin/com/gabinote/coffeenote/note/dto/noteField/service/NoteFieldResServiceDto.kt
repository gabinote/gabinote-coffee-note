package com.gabinote.coffeenote.note.dto.noteField.service

import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeResServiceDto

data class NoteFieldResServiceDto(
    var id: String,

    var name: String,

    var icon: String,

    var type: FieldType,

    var attributes: Set<AttributeResServiceDto> = emptySet(),

    var order: Int = 0,

    @JvmField
    var isDisplay: Boolean = true,

    var values: Set<String> = emptySet(),
)