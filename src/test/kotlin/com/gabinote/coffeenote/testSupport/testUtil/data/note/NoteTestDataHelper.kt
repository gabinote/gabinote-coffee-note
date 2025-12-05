package com.gabinote.coffeenote.testSupport.testUtil.data.note

import com.gabinote.coffeenote.note.domain.note.Note
import com.gabinote.coffeenote.note.domain.note.NoteDisplayField
import com.gabinote.coffeenote.note.domain.note.NoteField
import com.gabinote.coffeenote.note.domain.note.vo.NoteOwnedItem
import com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto
import com.gabinote.coffeenote.note.dto.note.controller.NoteResControllerDto
import com.gabinote.coffeenote.note.dto.note.controller.NoteUpdateReqControllerDto
import com.gabinote.coffeenote.note.dto.note.service.NoteCreateReqServiceDto
import com.gabinote.coffeenote.note.dto.note.service.NoteListResServiceDto
import com.gabinote.coffeenote.note.dto.note.service.NoteResServiceDto
import com.gabinote.coffeenote.note.dto.note.service.NoteUpdateReqServiceDto
import com.gabinote.coffeenote.note.dto.noteDisplayField.controller.NoteDisplayFieldResControllerDto
import com.gabinote.coffeenote.note.dto.noteDisplayField.service.NoteDisplayFieldResServiceDto
import com.gabinote.coffeenote.note.dto.noteField.controller.NoteFieldCreateReqControllerDto
import com.gabinote.coffeenote.note.dto.noteField.controller.NoteFieldResControllerDto
import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldCreateReqServiceDto
import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldResServiceDto
import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.util.*

object NoteTestDataHelper {

    fun createTestNote(
        id: ObjectId? = null,
        externalId: String = UUID.randomUUID().toString(),
        title: String = "테스트 노트",
        thumbnail: String? = null,
        createdDate: LocalDateTime? = LocalDateTime.now(),
        modifiedDate: LocalDateTime? = LocalDateTime.now(),
        fields: List<NoteField> = listOf(
            NoteFieldTestDataHelper.createTestNoteField()
        ),
        displayFields: List<NoteDisplayField> = listOf(
            NoteDisplayFieldTestDataHelper.createTestNoteDisplayField()
        ),
        isOpen: Boolean = false,
        owner: String = "test-owner",
        hash: String = NoteHashTestDataHelper.TEST_HASH,
    ) = Note(
        id = id,
        externalId = externalId,
        title = title,
        thumbnail = thumbnail,
        createdDate = createdDate,
        modifiedDate = modifiedDate,
        fields = fields,
        displayFields = displayFields,
        isOpen = isOpen,
        owner = owner,
        hash = hash
    )

    fun createTestNoteCreateReqControllerDto(
        title: String = "테스트 노트",
        thumbnail: String? = null,
        fields: List<NoteFieldCreateReqControllerDto> = listOf(
            NoteFieldTestDataHelper.createTestNoteFieldCreateReqControllerDto()
        ),
        isOpen: Boolean = false,
    ) = NoteCreateReqControllerDto(
        title = title,
        thumbnail = thumbnail,
        fields = fields,
        isOpen = isOpen,
    )

    fun createTestNoteUpdateReqControllerDto(
        title: String = "테스트 노트",
        thumbnail: String? = null,
        fields: List<NoteFieldCreateReqControllerDto> = listOf(
            NoteFieldTestDataHelper.createTestNoteFieldCreateReqControllerDto()
        ),
        isOpen: Boolean = false,
    ) = NoteUpdateReqControllerDto(
        title = title,
        thumbnail = thumbnail,
        fields = fields,
        isOpen = isOpen,
    )


    fun createTestNoteResControllerDto(
        externalId: UUID = UUID.randomUUID(),
        title: String = "테스트 노트",
        thumbnail: String? = null,
        createdDate: LocalDateTime = LocalDateTime.now(),
        modifiedDate: LocalDateTime = LocalDateTime.now(),
        fields: List<NoteFieldResControllerDto> = listOf(
            NoteFieldTestDataHelper.createTestNoteFieldControllerDto()
        ),
        displayFields: List<NoteDisplayFieldResControllerDto> = listOf(
            NoteDisplayFieldTestDataHelper.createTestNoteDisplayFieldControllerDto()
        ),
        isOpen: Boolean = false,
        owner: String = "test-owner",
    ) = NoteResControllerDto(
        externalId = externalId,
        title = title,
        thumbnail = thumbnail,
        createdDate = createdDate,
        modifiedDate = modifiedDate,
        fields = fields,
        displayFields = displayFields,
        isOpen = isOpen,
        owner = owner,
    )

    fun createTestNoteCreateReqServiceDto(
        title: String = "테스트 노트",
        thumbnail: String? = null,
        fields: List<NoteFieldCreateReqServiceDto> = listOf(
            NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto()
        ),
        isOpen: Boolean = false,
        owner: String = "test-owner",
    ) = NoteCreateReqServiceDto(
        title = title,
        thumbnail = thumbnail,
        fields = fields,
        isOpen = isOpen,
        owner = owner,
    )

    fun createTestNoteUpdateReqServiceDto(
        externalId: UUID = UUID.randomUUID(),
        title: String = "테스트 노트",
        thumbnail: String? = null,
        fields: List<NoteFieldCreateReqServiceDto> = listOf(
            NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto()
        ),
        isOpen: Boolean = false,
        owner: String = "test-owner",
    ) = NoteUpdateReqServiceDto(
        externalId = externalId,
        title = title,
        thumbnail = thumbnail,
        fields = fields,
        isOpen = isOpen,
        owner = owner,
    )

    fun createTestNoteResServiceDto(
        id: ObjectId = ObjectId(),
        externalId: UUID = UUID.randomUUID(),
        title: String = "테스트 노트",
        thumbnail: String? = null,
        createdDate: LocalDateTime = LocalDateTime.now(),
        modifiedDate: LocalDateTime = LocalDateTime.now(),
        fields: List<NoteFieldResServiceDto> = listOf(
            NoteFieldTestDataHelper.createTestNoteFieldServiceDto()
        ),
        displayFields: List<NoteDisplayFieldResServiceDto> = listOf(
            NoteDisplayFieldTestDataHelper.createTestNoteDisplayFieldResServiceDto()
        ),
        isOpen: Boolean = false,
        owner: String = "test-owner",
    ) = NoteResServiceDto(
        id = id,
        externalId = externalId,
        title = title,
        thumbnail = thumbnail,
        createdDate = createdDate,
        modifiedDate = modifiedDate,
        fields = fields,
        displayFields = displayFields,
        isOpen = isOpen,
        owner = owner,
    )

    fun createTestNoteListResServiceDto(
        id: ObjectId = ObjectId(),
        externalId: UUID = UUID.randomUUID(),
        title: String = "테스트 노트",
        thumbnail: String? = null,
        createdDate: LocalDateTime = LocalDateTime.now(),
        modifiedDate: LocalDateTime = LocalDateTime.now(),
        displayFields: List<NoteDisplayFieldResServiceDto> = listOf(
            NoteDisplayFieldTestDataHelper.createTestNoteDisplayFieldResServiceDto()
        ),
        isOpen: Boolean = false,
        owner: String = "test-owner",
    ) = NoteListResServiceDto(
        id = id,
        externalId = externalId,
        title = title,
        thumbnail = thumbnail,
        createdDate = createdDate,
        modifiedDate = modifiedDate,
        displayFields = displayFields,
        isOpen = isOpen,
        owner = owner,
    )

    fun createTestOwnedItem(
        id: ObjectId = ObjectId(),
        externalId: String = UUID.randomUUID().toString(),
        title: String = "테스트 노트",
        thumbnail: String? = null,
        createdDate: LocalDateTime? = LocalDateTime.now(),
        modifiedDate: LocalDateTime? = LocalDateTime.now(),
        displayFields: List<NoteDisplayField> = listOf(
            NoteDisplayFieldTestDataHelper.createTestNoteDisplayField()
        ),
        isOpen: Boolean = false,
        owner: String = "test-owner",
    ) = NoteOwnedItem(
        id = id,
        externalId = externalId,
        title = title,
        thumbnail = thumbnail,
        createdDate = createdDate,
        modifiedDate = modifiedDate,
        displayFields = displayFields,
        isOpen = isOpen,
        owner = owner,
    )

}
