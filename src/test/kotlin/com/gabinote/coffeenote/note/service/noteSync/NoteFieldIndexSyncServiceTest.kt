package com.gabinote.coffeenote.note.service.noteSync

import com.gabinote.coffeenote.note.domain.note.NoteStatus
import com.gabinote.coffeenote.note.dto.note.vo.NoteOnlyField
import com.gabinote.coffeenote.note.dto.noteFieldIndex.vo.NoteFieldIndexNoteIdHash
import com.gabinote.coffeenote.note.service.note.NoteService
import com.gabinote.coffeenote.note.service.noteFieldIndex.NoteFieldIndexService
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteFieldTestDataHelper.createTestNoteField
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteTestDataHelper.createTestNote
import io.mockk.*
import io.mockk.impl.annotations.MockK
import java.time.LocalDateTime
import java.util.*

class NoteFieldIndexSyncServiceTest : ServiceTestTemplate() {

    private lateinit var noteFieldIndexSyncService: NoteFieldIndexSyncService

    @MockK
    private lateinit var noteService: NoteService

    @MockK
    private lateinit var noteFieldIndexService: NoteFieldIndexService

    init {
        beforeTest {
            clearAllMocks()
            noteFieldIndexSyncService = NoteFieldIndexSyncService(
                noteFieldIndexService = noteFieldIndexService,
                noteService = noteService
            )
        }

        describe("[Note] NoteFieldIndexSyncService Test") {

            describe("NoteFieldIndexSyncService.sinkCurrentNotes") {

                context("정상 케이스 - Note와 FieldIndex가 일치하는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val noteId = UUID.randomUUID().toString()
                    val fieldId = "field_001"
                    val fieldName = "원산지"
                    val fieldValue = "에티오피아"

                    val noteOnlyField = NoteOnlyField(
                        externalId = noteId,
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.ACTIVE
                    )
                    val noteFieldIndexNoteIdHash = NoteFieldIndexNoteIdHash(
                        id = UUID.randomUUID().toString(),
                        noteId = noteId,
                        name = fieldName,
                        value = fieldValue,
                        fieldId = fieldId
                    )

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 1L
                        every {
                            noteService.getAllNoteFieldsWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(noteOnlyField)
                        every { noteFieldIndexService.getAllByNoteIds(listOf(noteId)) } returns listOf(
                            noteFieldIndexNoteIdHash
                        )
                    }

                    it("복구 작업이 수행되지 않는다") {
                        noteFieldIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 1) {
                            noteService.getCountWithBetweenModifiedDate(
                                syncRangeStart,
                                syncRangeEnd
                            )
                        }
                        verify(exactly = 1) {
                            noteService.getAllNoteFieldsWithBetweenModifiedDate(
                                any(),
                                any(),
                                any()
                            )
                        }
                        verify(exactly = 1) { noteFieldIndexService.getAllByNoteIds(listOf(noteId)) }

                        // 복구 작업이 수행되지 않아야 함
                        verify(exactly = 0) { noteFieldIndexService.createFromNote(any()) }
                        verify(exactly = 0) { noteFieldIndexService.deleteByNoteExtId(any()) }
                    }
                }

                context("NoteFieldIndexStatus.NOT_CREATED - 인덱싱이 되지 않은 노트가 있는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val noteId = UUID.randomUUID()
                    val fieldId = "field_001"
                    val fieldName = "원산지"
                    val fieldValue = "에티오피아"

                    val noteOnlyField = NoteOnlyField(
                        externalId = noteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.ACTIVE
                    )
                    val testNote = createTestNote(externalId = noteId.toString())

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 1L
                        every {
                            noteService.getAllNoteFieldsWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(noteOnlyField)
                        every { noteFieldIndexService.getAllByNoteIds(listOf(noteId.toString())) } returns emptyList()
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteFieldIndexService.createFromNote(testNote) } just runs
                    }

                    it("인덱스 생성 작업이 수행된다") {
                        noteFieldIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteFieldIndexService.createFromNote(testNote) }
                    }
                }

                context("NoteFieldIndexStatus.NOT_DELETED - 삭제된 노트의 인덱스가 남아있는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val noteId = UUID.randomUUID()
                    val fieldId = "field_001"
                    val fieldName = "원산지"
                    val fieldValue = "에티오피아"

                    val noteOnlyField = NoteOnlyField(
                        externalId = noteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.DELETED
                    )
                    val noteFieldIndexNoteIdHash = NoteFieldIndexNoteIdHash(
                        id = UUID.randomUUID().toString(),
                        noteId = noteId.toString(),
                        name = fieldName,
                        value = fieldValue,
                        fieldId = fieldId
                    )

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 1L
                        every {
                            noteService.getAllNoteFieldsWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(noteOnlyField)
                        every { noteFieldIndexService.getAllByNoteIds(listOf(noteId.toString())) } returns listOf(
                            noteFieldIndexNoteIdHash
                        )
                        every { noteFieldIndexService.deleteByNoteExtId(noteId) } just runs
                    }

                    it("인덱스 삭제 작업이 수행된다") {
                        noteFieldIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 1) { noteFieldIndexService.deleteByNoteExtId(noteId) }
                    }
                }

                context("NoteFieldIndexStatus.INVALID - Note와 FieldIndex의 필드 개수가 다른 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val noteId = UUID.randomUUID()
                    val fieldId1 = "field_001"
                    val fieldId2 = "field_002"
                    val fieldName1 = "원산지"
                    val fieldName2 = "로스팅"
                    val fieldValue1 = "에티오피아"
                    val fieldValue2 = "미디엄"

                    // Note에는 2개의 필드가 있음
                    val noteOnlyField = NoteOnlyField(
                        externalId = noteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId1,
                                name = fieldName1,
                                values = setOf(fieldValue1)
                            ),
                            createTestNoteField(
                                id = fieldId2,
                                name = fieldName2,
                                values = setOf(fieldValue2)
                            )
                        ),
                        status = NoteStatus.ACTIVE
                    )
                    // Index에는 1개의 필드만 있음
                    val noteFieldIndexNoteIdHash = NoteFieldIndexNoteIdHash(
                        id = UUID.randomUUID().toString(),
                        noteId = noteId.toString(),
                        name = fieldName1,
                        value = fieldValue1,
                        fieldId = fieldId1
                    )
                    val testNote = createTestNote(externalId = noteId.toString())

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 1L
                        every {
                            noteService.getAllNoteFieldsWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(noteOnlyField)
                        every { noteFieldIndexService.getAllByNoteIds(listOf(noteId.toString())) } returns listOf(
                            noteFieldIndexNoteIdHash
                        )
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteFieldIndexService.deleteByNoteExtId(noteId) } just runs
                        every { noteFieldIndexService.createFromNote(testNote) } just runs
                    }

                    it("인덱스 재생성 작업이 수행된다 (삭제 후 생성)") {
                        noteFieldIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteFieldIndexService.deleteByNoteExtId(noteId) }
                        verify(exactly = 1) { noteFieldIndexService.createFromNote(testNote) }
                    }
                }

                context("NoteFieldIndexStatus.INVALID - Note와 FieldIndex의 필드 값이 다른 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val noteId = UUID.randomUUID()
                    val fieldId = "field_001"
                    val fieldName = "원산지"
                    val noteFieldValue = "에티오피아"
                    val indexFieldValue = "콜롬비아"

                    val noteOnlyField = NoteOnlyField(
                        externalId = noteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(noteFieldValue)
                            )
                        ),
                        status = NoteStatus.ACTIVE
                    )
                    // Index에는 다른 값이 저장되어 있음
                    val noteFieldIndexNoteIdHash = NoteFieldIndexNoteIdHash(
                        id = UUID.randomUUID().toString(),
                        noteId = noteId.toString(),
                        name = fieldName,
                        value = indexFieldValue,
                        fieldId = fieldId
                    )
                    val testNote = createTestNote(externalId = noteId.toString())

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 1L
                        every {
                            noteService.getAllNoteFieldsWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(noteOnlyField)
                        every { noteFieldIndexService.getAllByNoteIds(listOf(noteId.toString())) } returns listOf(
                            noteFieldIndexNoteIdHash
                        )
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteFieldIndexService.deleteByNoteExtId(noteId) } just runs
                        every { noteFieldIndexService.createFromNote(testNote) } just runs
                    }

                    it("인덱스 재생성 작업이 수행된다 (삭제 후 생성)") {
                        noteFieldIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteFieldIndexService.deleteByNoteExtId(noteId) }
                        verify(exactly = 1) { noteFieldIndexService.createFromNote(testNote) }
                    }
                }

                context("NoteFieldIndexStatus.INVALID - Note와 FieldIndex의 필드 이름이 다른 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val noteId = UUID.randomUUID()
                    val fieldId = "field_001"
                    val noteFieldName = "원산지"
                    val indexFieldName = "로스팅"
                    val fieldValue = "에티오피아"

                    val noteOnlyField = NoteOnlyField(
                        externalId = noteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = noteFieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.ACTIVE
                    )
                    // Index에는 다른 이름이 저장되어 있음
                    val noteFieldIndexNoteIdHash = NoteFieldIndexNoteIdHash(
                        id = UUID.randomUUID().toString(),
                        noteId = noteId.toString(),
                        name = indexFieldName,
                        value = fieldValue,
                        fieldId = fieldId
                    )
                    val testNote = createTestNote(externalId = noteId.toString())

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 1L
                        every {
                            noteService.getAllNoteFieldsWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(noteOnlyField)
                        every { noteFieldIndexService.getAllByNoteIds(listOf(noteId.toString())) } returns listOf(
                            noteFieldIndexNoteIdHash
                        )
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteFieldIndexService.deleteByNoteExtId(noteId) } just runs
                        every { noteFieldIndexService.createFromNote(testNote) } just runs
                    }

                    it("인덱스 재생성 작업이 수행된다 (삭제 후 생성)") {
                        noteFieldIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteFieldIndexService.deleteByNoteExtId(noteId) }
                        verify(exactly = 1) { noteFieldIndexService.createFromNote(testNote) }
                    }
                }

                context("여러 종류의 상태를 가진 노트가 함께 있는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val fieldId = "field_001"
                    val fieldName = "원산지"
                    val fieldValue = "에티오피아"

                    // 정상 노트
                    val normalNoteId = UUID.randomUUID()
                    val normalNoteOnlyField = NoteOnlyField(
                        externalId = normalNoteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.ACTIVE
                    )
                    val normalNoteFieldIndex = NoteFieldIndexNoteIdHash(
                        id = UUID.randomUUID().toString(),
                        noteId = normalNoteId.toString(),
                        name = fieldName,
                        value = fieldValue,
                        fieldId = fieldId
                    )

                    // 인덱스 미생성 노트
                    val notCreatedNoteId = UUID.randomUUID()
                    val notCreatedNoteOnlyField = NoteOnlyField(
                        externalId = notCreatedNoteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.ACTIVE
                    )
                    val notCreatedTestNote = createTestNote(externalId = notCreatedNoteId.toString())

                    // 삭제되었으나 인덱스 남은 노트
                    val notDeletedNoteId = UUID.randomUUID()
                    val notDeletedNoteOnlyField = NoteOnlyField(
                        externalId = notDeletedNoteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.DELETED
                    )
                    val notDeletedNoteFieldIndex = NoteFieldIndexNoteIdHash(
                        id = UUID.randomUUID().toString(),
                        noteId = notDeletedNoteId.toString(),
                        name = fieldName,
                        value = fieldValue,
                        fieldId = fieldId
                    )

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 3L
                        every {
                            noteService.getAllNoteFieldsWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(normalNoteOnlyField, notCreatedNoteOnlyField, notDeletedNoteOnlyField)
                        every {
                            noteFieldIndexService.getAllByNoteIds(any())
                        } returns listOf(normalNoteFieldIndex, notDeletedNoteFieldIndex)
                        every { noteService.fetchByExternalId(notCreatedNoteId) } returns notCreatedTestNote
                        every { noteFieldIndexService.createFromNote(notCreatedTestNote) } just runs
                        every { noteFieldIndexService.deleteByNoteExtId(notDeletedNoteId) } just runs
                    }

                    it("각 상태에 맞는 복구 작업이 수행된다") {
                        noteFieldIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        // 정상 노트는 복구 작업 없음
                        verify(exactly = 0) { noteService.fetchByExternalId(normalNoteId) }

                        // 인덱스 미생성 노트는 인덱스 생성
                        verify(exactly = 1) { noteService.fetchByExternalId(notCreatedNoteId) }
                        verify(exactly = 1) { noteFieldIndexService.createFromNote(notCreatedTestNote) }

                        // 삭제된 노트의 인덱스는 삭제
                        verify(exactly = 1) { noteFieldIndexService.deleteByNoteExtId(notDeletedNoteId) }
                    }
                }

                context("처리할 노트가 없는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 0L
                    }

                    it("배치 처리가 수행되지 않는다") {
                        noteFieldIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 1) {
                            noteService.getCountWithBetweenModifiedDate(
                                syncRangeStart,
                                syncRangeEnd
                            )
                        }
                        verify(exactly = 0) {
                            noteService.getAllNoteFieldsWithBetweenModifiedDate(
                                any(),
                                any(),
                                any()
                            )
                        }
                    }
                }

                context("배치 크기보다 많은 노트가 있는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val batchSize = 2
                    val totalCount = 5L
                    val fieldId = "field_001"
                    val fieldName = "원산지"
                    val fieldValue = "에티오피아"

                    val noteIds = (1..5).map { UUID.randomUUID() }
                    val noteOnlyFields = noteIds.map {
                        NoteOnlyField(
                            externalId = it.toString(),
                            fields = listOf(
                                createTestNoteField(
                                    id = fieldId,
                                    name = fieldName,
                                    values = setOf(fieldValue)
                                )
                            ),
                            status = NoteStatus.ACTIVE
                        )
                    }
                    val noteFieldIndexes = noteIds.map {
                        NoteFieldIndexNoteIdHash(
                            id = UUID.randomUUID().toString(),
                            noteId = it.toString(),
                            name = fieldName,
                            value = fieldValue,
                            fieldId = fieldId
                        )
                    }

                    beforeTest {
                        every {
                            noteService.getCountWithBetweenModifiedDate(
                                syncRangeStart,
                                syncRangeEnd
                            )
                        } returns totalCount
                        every {
                            noteService.getAllNoteFieldsWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = match { it.pageNumber == 0 && it.pageSize == batchSize }
                            )
                        } returns noteOnlyFields.take(2)
                        every {
                            noteService.getAllNoteFieldsWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = match { it.pageNumber == 1 && it.pageSize == batchSize }
                            )
                        } returns noteOnlyFields.drop(2).take(2)
                        every {
                            noteService.getAllNoteFieldsWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = match { it.pageNumber == 2 && it.pageSize == batchSize }
                            )
                        } returns noteOnlyFields.drop(4)
                        every { noteFieldIndexService.getAllByNoteIds(any()) } answers {
                            val ids = firstArg<List<String>>()
                            noteFieldIndexes.filter { ids.contains(it.noteId) }
                        }
                    }

                    it("여러 배치로 나뉘어 처리된다") {
                        noteFieldIndexSyncService.sinkCurrentNotes(
                            batchSize = batchSize,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 3) {
                            noteService.getAllNoteFieldsWithBetweenModifiedDate(
                                any(),
                                any(),
                                any()
                            )
                        }
                    }
                }

                context("인덱스 생성 중 예외가 발생하는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val noteId = UUID.randomUUID()
                    val fieldId = "field_001"
                    val fieldName = "원산지"
                    val fieldValue = "에티오피아"

                    val noteOnlyField = NoteOnlyField(
                        externalId = noteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.ACTIVE
                    )
                    val testNote = createTestNote(externalId = noteId.toString())

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 1L
                        every {
                            noteService.getAllNoteFieldsWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(noteOnlyField)
                        every { noteFieldIndexService.getAllByNoteIds(listOf(noteId.toString())) } returns emptyList()
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteFieldIndexService.createFromNote(testNote) } throws RuntimeException("Index creation failed")
                    }

                    it("예외가 로깅되고 프로세스는 계속된다") {
                        // 예외가 발생해도 프로세스가 중단되지 않아야 함
                        noteFieldIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteFieldIndexService.createFromNote(testNote) }
                    }
                }

                context("삭제된 노트에 대한 인덱스가 없는 정상 상태인 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val noteId = UUID.randomUUID()
                    val fieldId = "field_001"
                    val fieldName = "원산지"
                    val fieldValue = "에티오피아"

                    val noteOnlyField = NoteOnlyField(
                        externalId = noteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.DELETED
                    )

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 1L
                        every {
                            noteService.getAllNoteFieldsWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(noteOnlyField)
                        every { noteFieldIndexService.getAllByNoteIds(listOf(noteId.toString())) } returns emptyList()
                    }

                    it("복구 작업이 수행되지 않는다") {
                        noteFieldIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 0) { noteFieldIndexService.createFromNote(any()) }
                        verify(exactly = 0) { noteFieldIndexService.deleteByNoteExtId(any()) }
                        verify(exactly = 0) { noteService.fetchByExternalId(any()) }
                    }
                }
            }

            describe("NoteFieldIndexSyncService.sinkAllNotes") {

                context("정상 케이스 - Note와 FieldIndex가 일치하는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val noteId = UUID.randomUUID().toString()
                    val fieldId = "field_001"
                    val fieldName = "원산지"
                    val fieldValue = "에티오피아"

                    val noteOnlyField = NoteOnlyField(
                        externalId = noteId,
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.ACTIVE
                    )
                    val noteFieldIndexNoteIdHash = NoteFieldIndexNoteIdHash(
                        id = UUID.randomUUID().toString(),
                        noteId = noteId,
                        name = fieldName,
                        value = fieldValue,
                        fieldId = fieldId
                    )

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 1L
                        every {
                            noteService.getAllNoteFieldsBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = any()
                            )
                        } returns listOf(noteOnlyField)
                        every { noteFieldIndexService.getAllByNoteIds(listOf(noteId)) } returns listOf(
                            noteFieldIndexNoteIdHash
                        )
                    }

                    it("복구 작업이 수행되지 않는다") {
                        noteFieldIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 1) { noteService.getCountBeforeModifiedDate(syncRangeStart) }
                        verify(exactly = 1) { noteService.getAllNoteFieldsBeforeModifiedDate(any(), any()) }
                        verify(exactly = 1) { noteFieldIndexService.getAllByNoteIds(listOf(noteId)) }

                        // 복구 작업이 수행되지 않아야 함
                        verify(exactly = 0) { noteFieldIndexService.createFromNote(any()) }
                        verify(exactly = 0) { noteFieldIndexService.deleteByNoteExtId(any()) }
                    }
                }

                context("NoteFieldIndexStatus.NOT_CREATED - 인덱싱이 되지 않은 노트가 있는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val noteId = UUID.randomUUID()
                    val fieldId = "field_001"
                    val fieldName = "원산지"
                    val fieldValue = "에티오피아"

                    val noteOnlyField = NoteOnlyField(
                        externalId = noteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.ACTIVE
                    )
                    val testNote = createTestNote(externalId = noteId.toString())

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 1L
                        every {
                            noteService.getAllNoteFieldsBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = any()
                            )
                        } returns listOf(noteOnlyField)
                        every { noteFieldIndexService.getAllByNoteIds(listOf(noteId.toString())) } returns emptyList()
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteFieldIndexService.createFromNote(testNote) } just runs
                    }

                    it("인덱스 생성 작업이 수행된다") {
                        noteFieldIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteFieldIndexService.createFromNote(testNote) }
                    }
                }

                context("NoteFieldIndexStatus.NOT_DELETED - 삭제된 노트의 인덱스가 남아있는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val noteId = UUID.randomUUID()
                    val fieldId = "field_001"
                    val fieldName = "원산지"
                    val fieldValue = "에티오피아"

                    val noteOnlyField = NoteOnlyField(
                        externalId = noteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.DELETED
                    )
                    val noteFieldIndexNoteIdHash = NoteFieldIndexNoteIdHash(
                        id = UUID.randomUUID().toString(),
                        noteId = noteId.toString(),
                        name = fieldName,
                        value = fieldValue,
                        fieldId = fieldId
                    )

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 1L
                        every {
                            noteService.getAllNoteFieldsBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = any()
                            )
                        } returns listOf(noteOnlyField)
                        every { noteFieldIndexService.getAllByNoteIds(listOf(noteId.toString())) } returns listOf(
                            noteFieldIndexNoteIdHash
                        )
                        every { noteFieldIndexService.deleteByNoteExtId(noteId) } just runs
                    }

                    it("인덱스 삭제 작업이 수행된다") {
                        noteFieldIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 1) { noteFieldIndexService.deleteByNoteExtId(noteId) }
                    }
                }

                context("NoteFieldIndexStatus.INVALID - Note와 FieldIndex의 필드 개수가 다른 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val noteId = UUID.randomUUID()
                    val fieldId1 = "field_001"
                    val fieldId2 = "field_002"
                    val fieldName1 = "원산지"
                    val fieldName2 = "로스팅"
                    val fieldValue1 = "에티오피아"
                    val fieldValue2 = "미디엄"

                    // Note에는 2개의 필드가 있음
                    val noteOnlyField = NoteOnlyField(
                        externalId = noteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId1,
                                name = fieldName1,
                                values = setOf(fieldValue1)
                            ),
                            createTestNoteField(
                                id = fieldId2,
                                name = fieldName2,
                                values = setOf(fieldValue2)
                            )
                        ),
                        status = NoteStatus.ACTIVE
                    )
                    // Index에는 1개의 필드만 있음
                    val noteFieldIndexNoteIdHash = NoteFieldIndexNoteIdHash(
                        id = UUID.randomUUID().toString(),
                        noteId = noteId.toString(),
                        name = fieldName1,
                        value = fieldValue1,
                        fieldId = fieldId1
                    )
                    val testNote = createTestNote(externalId = noteId.toString())

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 1L
                        every {
                            noteService.getAllNoteFieldsBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = any()
                            )
                        } returns listOf(noteOnlyField)
                        every { noteFieldIndexService.getAllByNoteIds(listOf(noteId.toString())) } returns listOf(
                            noteFieldIndexNoteIdHash
                        )
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteFieldIndexService.deleteByNoteExtId(noteId) } just runs
                        every { noteFieldIndexService.createFromNote(testNote) } just runs
                    }

                    it("인덱스 재생성 작업이 수행된다 (삭제 후 생성)") {
                        noteFieldIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteFieldIndexService.deleteByNoteExtId(noteId) }
                        verify(exactly = 1) { noteFieldIndexService.createFromNote(testNote) }
                    }
                }

                context("처리할 노트가 없는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 0L
                    }

                    it("배치 처리가 수행되지 않는다") {
                        noteFieldIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 1) { noteService.getCountBeforeModifiedDate(syncRangeStart) }
                        verify(exactly = 0) { noteService.getAllNoteFieldsBeforeModifiedDate(any(), any()) }
                    }
                }

                context("배치 크기보다 많은 노트가 있는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val batchSize = 2
                    val totalCount = 5L
                    val fieldId = "field_001"
                    val fieldName = "원산지"
                    val fieldValue = "에티오피아"

                    val noteIds = (1..5).map { UUID.randomUUID() }
                    val noteOnlyFields = noteIds.map {
                        NoteOnlyField(
                            externalId = it.toString(),
                            fields = listOf(
                                createTestNoteField(
                                    id = fieldId,
                                    name = fieldName,
                                    values = setOf(fieldValue)
                                )
                            ),
                            status = NoteStatus.ACTIVE
                        )
                    }
                    val noteFieldIndexes = noteIds.map {
                        NoteFieldIndexNoteIdHash(
                            id = UUID.randomUUID().toString(),
                            noteId = it.toString(),
                            name = fieldName,
                            value = fieldValue,
                            fieldId = fieldId
                        )
                    }

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns totalCount
                        every {
                            noteService.getAllNoteFieldsBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = match { it.pageNumber == 0 && it.pageSize == batchSize }
                            )
                        } returns noteOnlyFields.take(2)
                        every {
                            noteService.getAllNoteFieldsBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = match { it.pageNumber == 1 && it.pageSize == batchSize }
                            )
                        } returns noteOnlyFields.drop(2).take(2)
                        every {
                            noteService.getAllNoteFieldsBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = match { it.pageNumber == 2 && it.pageSize == batchSize }
                            )
                        } returns noteOnlyFields.drop(4)
                        every { noteFieldIndexService.getAllByNoteIds(any()) } answers {
                            val ids = firstArg<List<String>>()
                            noteFieldIndexes.filter { ids.contains(it.noteId) }
                        }
                    }

                    it("여러 배치로 나뉘어 처리된다") {
                        noteFieldIndexSyncService.sinkAllNotes(
                            batchSize = batchSize,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 3) { noteService.getAllNoteFieldsBeforeModifiedDate(any(), any()) }
                    }
                }

                context("인덱스 생성 중 예외가 발생하는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val noteId = UUID.randomUUID()
                    val fieldId = "field_001"
                    val fieldName = "원산지"
                    val fieldValue = "에티오피아"

                    val noteOnlyField = NoteOnlyField(
                        externalId = noteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.ACTIVE
                    )
                    val testNote = createTestNote(externalId = noteId.toString())

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 1L
                        every {
                            noteService.getAllNoteFieldsBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = any()
                            )
                        } returns listOf(noteOnlyField)
                        every { noteFieldIndexService.getAllByNoteIds(listOf(noteId.toString())) } returns emptyList()
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteFieldIndexService.createFromNote(testNote) } throws RuntimeException("Index creation failed")
                    }

                    it("예외가 로깅되고 프로세스는 계속된다") {
                        // 예외가 발생해도 프로세스가 중단되지 않아야 함
                        noteFieldIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteFieldIndexService.createFromNote(testNote) }
                    }
                }

                context("삭제된 노트에 대한 인덱스가 없는 정상 상태인 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val noteId = UUID.randomUUID()
                    val fieldId = "field_001"
                    val fieldName = "원산지"
                    val fieldValue = "에티오피아"

                    val noteOnlyField = NoteOnlyField(
                        externalId = noteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.DELETED
                    )

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 1L
                        every {
                            noteService.getAllNoteFieldsBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = any()
                            )
                        } returns listOf(noteOnlyField)
                        every { noteFieldIndexService.getAllByNoteIds(listOf(noteId.toString())) } returns emptyList()
                    }

                    it("복구 작업이 수행되지 않는다") {
                        noteFieldIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 0) { noteFieldIndexService.createFromNote(any()) }
                        verify(exactly = 0) { noteFieldIndexService.deleteByNoteExtId(any()) }
                        verify(exactly = 0) { noteService.fetchByExternalId(any()) }
                    }
                }

                context("여러 종류의 상태를 가진 노트가 함께 있는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val fieldId = "field_001"
                    val fieldName = "원산지"
                    val fieldValue = "에티오피아"

                    // 정상 노트
                    val normalNoteId = UUID.randomUUID()
                    val normalNoteOnlyField = NoteOnlyField(
                        externalId = normalNoteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.ACTIVE
                    )
                    val normalNoteFieldIndex = NoteFieldIndexNoteIdHash(
                        id = UUID.randomUUID().toString(),
                        noteId = normalNoteId.toString(),
                        name = fieldName,
                        value = fieldValue,
                        fieldId = fieldId
                    )

                    // 인덱스 미생성 노트
                    val notCreatedNoteId = UUID.randomUUID()
                    val notCreatedNoteOnlyField = NoteOnlyField(
                        externalId = notCreatedNoteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.ACTIVE
                    )
                    val notCreatedTestNote = createTestNote(externalId = notCreatedNoteId.toString())

                    // 삭제되었으나 인덱스 남은 노트
                    val notDeletedNoteId = UUID.randomUUID()
                    val notDeletedNoteOnlyField = NoteOnlyField(
                        externalId = notDeletedNoteId.toString(),
                        fields = listOf(
                            createTestNoteField(
                                id = fieldId,
                                name = fieldName,
                                values = setOf(fieldValue)
                            )
                        ),
                        status = NoteStatus.DELETED
                    )
                    val notDeletedNoteFieldIndex = NoteFieldIndexNoteIdHash(
                        id = UUID.randomUUID().toString(),
                        noteId = notDeletedNoteId.toString(),
                        name = fieldName,
                        value = fieldValue,
                        fieldId = fieldId
                    )

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 3L
                        every {
                            noteService.getAllNoteFieldsBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = any()
                            )
                        } returns listOf(normalNoteOnlyField, notCreatedNoteOnlyField, notDeletedNoteOnlyField)
                        every {
                            noteFieldIndexService.getAllByNoteIds(any())
                        } returns listOf(normalNoteFieldIndex, notDeletedNoteFieldIndex)
                        every { noteService.fetchByExternalId(notCreatedNoteId) } returns notCreatedTestNote
                        every { noteFieldIndexService.createFromNote(notCreatedTestNote) } just runs
                        every { noteFieldIndexService.deleteByNoteExtId(notDeletedNoteId) } just runs
                    }

                    it("각 상태에 맞는 복구 작업이 수행된다") {
                        noteFieldIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        // 정상 노트는 복구 작업 없음
                        verify(exactly = 0) { noteService.fetchByExternalId(normalNoteId) }

                        // 인덱스 미생성 노트는 인덱스 생성
                        verify(exactly = 1) { noteService.fetchByExternalId(notCreatedNoteId) }
                        verify(exactly = 1) { noteFieldIndexService.createFromNote(notCreatedTestNote) }

                        // 삭제된 노트의 인덱스는 삭제
                        verify(exactly = 1) { noteFieldIndexService.deleteByNoteExtId(notDeletedNoteId) }
                    }
                }
            }
        }
    }
}

