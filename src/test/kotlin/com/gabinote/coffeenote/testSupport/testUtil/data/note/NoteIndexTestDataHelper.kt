package com.gabinote.coffeenote.testSupport.testUtil.data.note

import com.gabinote.coffeenote.note.dto.noteIndex.controller.NoteIndexResControllerDto
import com.gabinote.coffeenote.note.dto.noteIndex.service.NoteIndexResServiceDto
import com.gabinote.coffeenote.note.dto.noteIndexDisplayField.controller.IndexDisplayFieldResControllerDto
import com.gabinote.coffeenote.note.dto.noteIndexDisplayField.service.IndexDisplayFieldResServiceDto
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteDisplayFieldTestDataHelper.createTestIndexDisplayFieldResControllerDto
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteDisplayFieldTestDataHelper.createTestIndexDisplayFieldResServiceDto
import java.time.LocalDateTime
import java.util.*

object NoteIndexTestDataHelper {

    fun createTestNoteIndexResServiceDto(
        id: String = UUID.randomUUID().toString(),
        title: String = "테스트 노트 인덱스",
        owner: String = "test-owner",
        createdDate: LocalDateTime = LocalDateTime.now(),
        modifiedDate: LocalDateTime = LocalDateTime.now(),
        displayFields: List<IndexDisplayFieldResServiceDto> = listOf(
            createTestIndexDisplayFieldResServiceDto()
        ),
        filters: Map<String, List<String>> = emptyMap(),
    ) = NoteIndexResServiceDto(
        id = id,
        title = title,
        owner = owner,
        createdDate = createdDate,
        modifiedDate = modifiedDate,
        displayFields = displayFields,
        filters = filters,
    )

    fun createTestNoteIndexResControllerDto(
        id: String = UUID.randomUUID().toString(),
        title: String = "테스트 노트 인덱스",
        owner: String = "test-owner",
        createdDate: LocalDateTime = LocalDateTime.now(),
        modifiedDate: LocalDateTime = LocalDateTime.now(),
        displayFields: List<IndexDisplayFieldResControllerDto> = listOf(
            createTestIndexDisplayFieldResControllerDto()
        ),
        filters: Map<String, List<String>> = emptyMap(),
    ) = NoteIndexResControllerDto(
        id = id,
        title = title,
        owner = owner,
        createdDate = createdDate,
        modifiedDate = modifiedDate,
        displayFields = displayFields,
        filters = filters,
    )
}

