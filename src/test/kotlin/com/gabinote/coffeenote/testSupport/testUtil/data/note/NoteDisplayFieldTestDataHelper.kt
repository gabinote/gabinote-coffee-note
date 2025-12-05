package com.gabinote.coffeenote.testSupport.testUtil.data.note

import com.gabinote.coffeenote.note.domain.note.NoteDisplayField
import com.gabinote.coffeenote.note.dto.noteDisplayField.controller.NoteDisplayFieldResControllerDto
import com.gabinote.coffeenote.note.dto.noteDisplayField.service.NoteDisplayFieldResServiceDto
import com.gabinote.coffeenote.note.dto.noteIndexDisplayField.service.IndexDisplayFieldResServiceDto

object NoteDisplayFieldTestDataHelper {
    fun createTestNoteDisplayField(
        name: String = "원산지",
        icon: String = "globe",
        values: Set<String> = setOf("에티오피아"),
        order: Int = 0,
    ) = NoteDisplayField(
        name = name,
        icon = icon,
        values = values.toSet(),
        order = order,
    )

    fun createTestNoteDisplayFieldControllerDto(
        name: String = "원산지",
        icon: String = "globe",
        values: Set<String> = setOf("에티오피아"),
        order: Int = 0,
    ) = NoteDisplayFieldResControllerDto(
        name = name,
        icon = icon,
        values = values.toSet(),
        order = order,
    )

    fun createTestIndexDisplayFieldResServiceDto(
        name: String = "원산지",
        tag: String = "원산지",
        value: List<String> = listOf("에티오피아"),
        order: Int = 0,
    ) = IndexDisplayFieldResServiceDto(
        name = name,
        tag = tag,
        value = value,
        order = order,
    )

    // New: service DTO helper for NoteDisplayField
    fun createTestNoteDisplayFieldResServiceDto(
        name: String = "원산지",
        icon: String = "globe",
        values: Set<String> = setOf("에티오피아"),
        order: Int = 0,
    ) = NoteDisplayFieldResServiceDto(
        name = name,
        icon = icon,
        values = values.toSet(),
        order = order,
    )
}
