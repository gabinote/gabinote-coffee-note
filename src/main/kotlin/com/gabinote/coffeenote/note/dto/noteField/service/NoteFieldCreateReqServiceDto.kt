package com.gabinote.coffeenote.note.dto.noteField.service

import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto

data class NoteFieldCreateReqServiceDto(
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