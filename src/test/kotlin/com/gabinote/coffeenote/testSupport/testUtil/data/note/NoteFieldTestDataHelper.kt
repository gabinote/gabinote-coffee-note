package com.gabinote.coffeenote.testSupport.testUtil.data.note

import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeResControllerDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeResServiceDto
import com.gabinote.coffeenote.note.domain.note.NoteField
import com.gabinote.coffeenote.note.dto.noteField.controller.NoteFieldCreateReqControllerDto
import com.gabinote.coffeenote.note.dto.noteField.controller.NoteFieldResControllerDto
import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldCreateReqServiceDto
import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldResServiceDto
import com.gabinote.coffeenote.testSupport.testUtil.data.field.AttributeTestDataHelper
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldType

object NoteFieldTestDataHelper {
    fun createTestNoteField(
        id: String = "field_001",
        name: String = "원산지",
        icon: String = "globe",
        attributes: Set<AttributeResControllerDto> = emptySet(),
        order: Int = 1,
        isDisplay: Boolean = true,
        values: Set<String> = setOf("에티오피아"),
    ): NoteField {
        return NoteField(
            id = id,
            name = name,
            icon = icon,
            type = TestFieldType.getKeyString(),
            attributes = attributes.map {
                AttributeTestDataHelper.createTestAttribute(
                    key = it.key,
                    value = it.value
                )
            }.toSet(),
            order = order,
            isDisplay = isDisplay,
            values = values.toSet()
        )
    }

    fun createTestNoteFieldControllerDto(
        id: String = "d6283550-a30a-455e-a522-8ba486a1ae7f",
        name: String = "원산지",
        icon: String = "globe",
        type: FieldType = TestFieldType,
        attributes: Set<AttributeResControllerDto> = setOf(
            AttributeResControllerDto(key = "test", value = setOf("test"))
        ),
        order: Int = 1,
        isDisplay: Boolean = true,
        values: Set<String> = setOf("에티오피아"),
    ) = NoteFieldResControllerDto(
        id = id,
        name = name,
        icon = icon,
        type = type,
        attributes = attributes.toSet(),
        order = order,
        isDisplay = isDisplay,
        values = values.toSet()
    )

    fun createTestNoteFieldServiceDto(
        id: String = "d6283550-a30a-455e-a522-8ba486a1ae7f",
        name: String = "원산지",
        icon: String = "globe",
        type: FieldType = TestFieldType,
        attributes: Set<AttributeResServiceDto> = setOf(
            AttributeTestDataHelper.createTestAttributeResServiceDto()
        ),
        order: Int = 1,
        isDisplay: Boolean = true,
        values: Set<String> = setOf("에티오피아"),
    ) = NoteFieldResServiceDto(
        id = id,
        name = name,
        icon = icon,
        type = type,
        attributes = attributes.toSet(),
        order = order,
        isDisplay = isDisplay,
        values = values.toSet()
    )

    fun createTestNoteFieldCreateReqControllerDto(
        id: String = "d6283550-a30a-455e-a522-8ba486a1ae7f",
        name: String = "원산지",
        icon: String = "globe",
        type: FieldType = TestFieldType,
        attributes: Set<AttributeCreateReqServiceDto> = emptySet(),
        order: Int = 1,
        isDisplay: Boolean = true,
        values: Set<String> = setOf("에티오피아"),
    ) = NoteFieldCreateReqControllerDto(
        id = id,
        name = name,
        icon = icon,
        type = type,
        attributes = attributes.toSet(),
        order = order,
        isDisplay = isDisplay,
        values = values.toSet()
    )

    // New: service-side create request DTO helper
    fun createTestNoteFieldCreateReqServiceDto(
        id: String = "d6283550-a30a-455e-a522-8ba486a1ae7f",
        name: String = "원산지",
        icon: String = "globe",
        type: FieldType = TestFieldType,
        attributes: Set<AttributeCreateReqServiceDto> = emptySet(),
        order: Int = 1,
        isDisplay: Boolean = true,
        values: Set<String> = setOf("에티오피아"),
    ) = NoteFieldCreateReqServiceDto(
        id = id,
        name = name,
        icon = icon,
        type = type,
        attributes = attributes.toSet(),
        order = order,
        isDisplay = isDisplay,
        values = values.toSet()
    )
}