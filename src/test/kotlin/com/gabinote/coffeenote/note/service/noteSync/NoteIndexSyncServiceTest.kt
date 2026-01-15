package com.gabinote.coffeenote.note.service.noteSync

import com.gabinote.coffeenote.note.domain.note.NoteStatus
import com.gabinote.coffeenote.note.dto.note.vo.NoteExtIdHash
import com.gabinote.coffeenote.note.dto.noteIndex.vo.NoteIndexIdHash
import com.gabinote.coffeenote.note.service.note.NoteService
import com.gabinote.coffeenote.note.service.noteIndex.NoteIndexService
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteTestDataHelper.createTestNote
import io.mockk.*
import io.mockk.impl.annotations.MockK
import java.time.LocalDateTime
import java.util.*

class NoteIndexSyncServiceTest : ServiceTestTemplate() {

    private lateinit var noteIndexSyncService: NoteIndexSyncService

    @MockK
    private lateinit var noteService: NoteService

    @MockK
    private lateinit var noteIndexService: NoteIndexService

    init {
        beforeTest {
            clearAllMocks()
            noteIndexSyncService = NoteIndexSyncService(
                noteIndexService = noteIndexService,
                noteService = noteService
            )
        }

        describe("[Note] NoteIndexSyncService Test") {

            describe("NoteIndexSyncService.sinkCurrentNotes") {

                context("정상 케이스 - Note와 Index가 일치하는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val noteId = UUID.randomUUID().toString()
                    val noteHash = "test-hash-123"

                    val noteExtIdHash = NoteExtIdHash(
                        externalId = noteId,
                        hash = noteHash,
                        status = NoteStatus.ACTIVE
                    )
                    val noteIndexIdHash = NoteIndexIdHash(
                        id = noteId,
                        noteHash = noteHash
                    )

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 1L
                        every {
                            noteService.getAllNoteExtIdHashWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(noteExtIdHash)
                        every { noteIndexService.getAllByIds(listOf(noteId)) } returns listOf(noteIndexIdHash)
                    }

                    it("복구 작업이 수행되지 않는다") {
                        noteIndexSyncService.sinkCurrentNotes(
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
                            noteService.getAllNoteExtIdHashWithBetweenModifiedDate(
                                any(),
                                any(),
                                any()
                            )
                        }
                        verify(exactly = 1) { noteIndexService.getAllByIds(listOf(noteId)) }

                        // 복구 작업이 수행되지 않아야 함
                        verify(exactly = 0) { noteIndexService.createFromNote(any()) }
                        verify(exactly = 0) { noteIndexService.deleteByNoteId(any()) }
                    }
                }

                context("NoteIndexStatus.NOT_FOUND - 인덱싱이 되지 않은 노트가 있는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val noteId = UUID.randomUUID()
                    val noteHash = "test-hash-123"

                    val noteExtIdHash = NoteExtIdHash(
                        externalId = noteId.toString(),
                        hash = noteHash,
                        status = NoteStatus.ACTIVE
                    )
                    val testNote = createTestNote(externalId = noteId.toString(), hash = noteHash)

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 1L
                        every {
                            noteService.getAllNoteExtIdHashWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(noteExtIdHash)
                        every { noteIndexService.getAllByIds(listOf(noteId.toString())) } returns emptyList()
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteIndexService.createFromNote(testNote) } just runs
                    }

                    it("인덱스 생성 작업이 수행된다") {
                        noteIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteIndexService.createFromNote(testNote) }
                    }
                }

                context("NoteIndexStatus.NOT_DELETED - 삭제된 노트의 인덱스가 남아있는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val noteId = UUID.randomUUID()
                    val noteHash = "test-hash-123"

                    val noteExtIdHash = NoteExtIdHash(
                        externalId = noteId.toString(),
                        hash = noteHash,
                        status = NoteStatus.DELETED
                    )
                    val noteIndexIdHash = NoteIndexIdHash(
                        id = noteId.toString(),
                        noteHash = noteHash
                    )

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 1L
                        every {
                            noteService.getAllNoteExtIdHashWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(noteExtIdHash)
                        every { noteIndexService.getAllByIds(listOf(noteId.toString())) } returns listOf(noteIndexIdHash)
                        every { noteIndexService.deleteByNoteId(noteId) } just runs
                    }

                    it("인덱스 삭제 작업이 수행된다") {
                        noteIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 1) { noteIndexService.deleteByNoteId(noteId) }
                    }
                }

                context("NoteIndexStatus.INVALID - Note와 Index의 해시가 다른 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val noteId = UUID.randomUUID()
                    val noteHash = "new-note-hash"
                    val indexHash = "old-index-hash"

                    val noteExtIdHash = NoteExtIdHash(
                        externalId = noteId.toString(),
                        hash = noteHash,
                        status = NoteStatus.ACTIVE
                    )
                    val noteIndexIdHash = NoteIndexIdHash(
                        id = noteId.toString(),
                        noteHash = indexHash
                    )
                    val testNote = createTestNote(externalId = noteId.toString(), hash = noteHash)

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 1L
                        every {
                            noteService.getAllNoteExtIdHashWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(noteExtIdHash)
                        every { noteIndexService.getAllByIds(listOf(noteId.toString())) } returns listOf(noteIndexIdHash)
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteIndexService.deleteByNoteId(noteId) } just runs
                        every { noteIndexService.createFromNote(testNote) } just runs
                    }

                    it("인덱스 재생성 작업이 수행된다 (삭제 후 생성)") {
                        noteIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteIndexService.deleteByNoteId(noteId) }
                        verify(exactly = 1) { noteIndexService.createFromNote(testNote) }
                    }
                }

                context("NoteIndexStatus.MULTI_EXISTS - 동일 노트에 대한 인덱스가 여러 개 존재하는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val noteId = UUID.randomUUID()
                    val noteHash = "test-hash-123"

                    val noteExtIdHash = NoteExtIdHash(
                        externalId = noteId.toString(),
                        hash = noteHash,
                        status = NoteStatus.ACTIVE
                    )
                    // 동일 노트에 대한 여러 인덱스 엔트리
                    val noteIndexIdHash1 = NoteIndexIdHash(
                        id = noteId.toString(),
                        noteHash = noteHash
                    )
                    val noteIndexIdHash2 = NoteIndexIdHash(
                        id = noteId.toString(),
                        noteHash = "different-hash"
                    )
                    val testNote = createTestNote(externalId = noteId.toString(), hash = noteHash)

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 1L
                        every {
                            noteService.getAllNoteExtIdHashWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(noteExtIdHash)
                        every { noteIndexService.getAllByIds(listOf(noteId.toString())) } returns listOf(
                            noteIndexIdHash1,
                            noteIndexIdHash2
                        )
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteIndexService.deleteByNoteId(noteId) } just runs
                        every { noteIndexService.createFromNote(testNote) } just runs
                    }

                    it("인덱스 재생성 작업이 수행된다 (삭제 후 생성)") {
                        noteIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteIndexService.deleteByNoteId(noteId) }
                        verify(exactly = 1) { noteIndexService.createFromNote(testNote) }
                    }
                }

                context("여러 종류의 상태를 가진 노트가 함께 있는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)

                    // 정상 노트
                    val normalNoteId = UUID.randomUUID()
                    val normalNoteHash = "normal-hash"
                    val normalNoteExtIdHash = NoteExtIdHash(normalNoteId.toString(), normalNoteHash, NoteStatus.ACTIVE)
                    val normalNoteIndexIdHash = NoteIndexIdHash(normalNoteId.toString(), normalNoteHash)

                    // 인덱스 미생성 노트
                    val notFoundNoteId = UUID.randomUUID()
                    val notFoundNoteHash = "not-found-hash"
                    val notFoundNoteExtIdHash =
                        NoteExtIdHash(notFoundNoteId.toString(), notFoundNoteHash, NoteStatus.ACTIVE)
                    val notFoundTestNote =
                        createTestNote(externalId = notFoundNoteId.toString(), hash = notFoundNoteHash)

                    // 삭제되었으나 인덱스 남은 노트
                    val notDeletedNoteId = UUID.randomUUID()
                    val notDeletedNoteHash = "not-deleted-hash"
                    val notDeletedNoteExtIdHash =
                        NoteExtIdHash(notDeletedNoteId.toString(), notDeletedNoteHash, NoteStatus.DELETED)
                    val notDeletedNoteIndexIdHash = NoteIndexIdHash(notDeletedNoteId.toString(), notDeletedNoteHash)

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 3L
                        every {
                            noteService.getAllNoteExtIdHashWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(normalNoteExtIdHash, notFoundNoteExtIdHash, notDeletedNoteExtIdHash)
                        every {
                            noteIndexService.getAllByIds(any())
                        } returns listOf(normalNoteIndexIdHash, notDeletedNoteIndexIdHash)
                        every { noteService.fetchByExternalId(notFoundNoteId) } returns notFoundTestNote
                        every { noteIndexService.createFromNote(notFoundTestNote) } just runs
                        every { noteIndexService.deleteByNoteId(notDeletedNoteId) } just runs
                    }

                    it("각 상태에 맞는 복구 작업이 수행된다") {
                        noteIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        // 정상 노트는 복구 작업 없음
                        verify(exactly = 0) { noteService.fetchByExternalId(normalNoteId) }

                        // 인덱스 미생성 노트는 인덱스 생성
                        verify(exactly = 1) { noteService.fetchByExternalId(notFoundNoteId) }
                        verify(exactly = 1) { noteIndexService.createFromNote(notFoundTestNote) }

                        // 삭제된 노트의 인덱스는 삭제
                        verify(exactly = 1) { noteIndexService.deleteByNoteId(notDeletedNoteId) }
                    }
                }

                context("처리할 노트가 없는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 0L
                    }

                    it("배치 처리가 수행되지 않는다") {
                        noteIndexSyncService.sinkCurrentNotes(
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
                            noteService.getAllNoteExtIdHashWithBetweenModifiedDate(
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

                    val noteIds = (1..5).map { UUID.randomUUID() }
                    val noteExtIdHashes = noteIds.map { NoteExtIdHash(it.toString(), "hash-$it", NoteStatus.ACTIVE) }
                    val noteIndexIdHashes = noteIds.map { NoteIndexIdHash(it.toString(), "hash-$it") }

                    beforeTest {
                        every {
                            noteService.getCountWithBetweenModifiedDate(
                                syncRangeStart,
                                syncRangeEnd
                            )
                        } returns totalCount
                        every {
                            noteService.getAllNoteExtIdHashWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = match { it.pageNumber == 0 && it.pageSize == batchSize }
                            )
                        } returns noteExtIdHashes.take(2)
                        every {
                            noteService.getAllNoteExtIdHashWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = match { it.pageNumber == 1 && it.pageSize == batchSize }
                            )
                        } returns noteExtIdHashes.drop(2).take(2)
                        every {
                            noteService.getAllNoteExtIdHashWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = match { it.pageNumber == 2 && it.pageSize == batchSize }
                            )
                        } returns noteExtIdHashes.drop(4)
                        every { noteIndexService.getAllByIds(any()) } answers {
                            val ids = firstArg<List<String>>()
                            noteIndexIdHashes.filter { ids.contains(it.id) }
                        }
                    }

                    it("여러 배치로 나뉘어 처리된다") {
                        noteIndexSyncService.sinkCurrentNotes(
                            batchSize = batchSize,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 3) {
                            noteService.getAllNoteExtIdHashWithBetweenModifiedDate(
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
                    val noteHash = "test-hash-123"

                    val noteExtIdHash = NoteExtIdHash(
                        externalId = noteId.toString(),
                        hash = noteHash,
                        status = NoteStatus.ACTIVE
                    )
                    val testNote = createTestNote(externalId = noteId.toString(), hash = noteHash)

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 1L
                        every {
                            noteService.getAllNoteExtIdHashWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(noteExtIdHash)
                        every { noteIndexService.getAllByIds(listOf(noteId.toString())) } returns emptyList()
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteIndexService.createFromNote(testNote) } throws RuntimeException("Index creation failed")
                    }

                    it("예외가 로깅되고 프로세스는 계속된다") {
                        // 예외가 발생해도 프로세스가 중단되지 않아야 함
                        noteIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteIndexService.createFromNote(testNote) }
                    }
                }

                context("삭제된 노트에 대한 인덱스가 없는 정상 상태인 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val syncRangeEnd = LocalDateTime.of(2025, 1, 2, 0, 0)
                    val noteId = UUID.randomUUID()
                    val noteHash = "test-hash-123"

                    val noteExtIdHash = NoteExtIdHash(
                        externalId = noteId.toString(),
                        hash = noteHash,
                        status = NoteStatus.DELETED
                    )

                    beforeTest {
                        every { noteService.getCountWithBetweenModifiedDate(syncRangeStart, syncRangeEnd) } returns 1L
                        every {
                            noteService.getAllNoteExtIdHashWithBetweenModifiedDate(
                                startDate = syncRangeStart,
                                endDate = syncRangeEnd,
                                pageable = any()
                            )
                        } returns listOf(noteExtIdHash)
                        every { noteIndexService.getAllByIds(listOf(noteId.toString())) } returns emptyList()
                    }

                    it("복구 작업이 수행되지 않는다") {
                        noteIndexSyncService.sinkCurrentNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart,
                            syncRangeEnd = syncRangeEnd
                        )

                        verify(exactly = 0) { noteIndexService.createFromNote(any()) }
                        verify(exactly = 0) { noteIndexService.deleteByNoteId(any()) }
                        verify(exactly = 0) { noteService.fetchByExternalId(any()) }
                    }
                }
            }

            describe("NoteIndexSyncService.sinkAllNotes") {

                context("정상 케이스 - Note와 Index가 일치하는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val noteId = UUID.randomUUID().toString()
                    val noteHash = "test-hash-123"

                    val noteExtIdHash = NoteExtIdHash(
                        externalId = noteId,
                        hash = noteHash,
                        status = NoteStatus.ACTIVE
                    )
                    val noteIndexIdHash = NoteIndexIdHash(
                        id = noteId,
                        noteHash = noteHash
                    )

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 1L
                        every {
                            noteService.getAllNoteExtIdHashBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = any()
                            )
                        } returns listOf(noteExtIdHash)
                        every { noteIndexService.getAllByIds(listOf(noteId)) } returns listOf(noteIndexIdHash)
                    }

                    it("복구 작업이 수행되지 않는다") {
                        noteIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 1) { noteService.getCountBeforeModifiedDate(syncRangeStart) }
                        verify(exactly = 1) { noteService.getAllNoteExtIdHashBeforeModifiedDate(any(), any()) }
                        verify(exactly = 1) { noteIndexService.getAllByIds(listOf(noteId)) }

                        // 복구 작업이 수행되지 않아야 함
                        verify(exactly = 0) { noteIndexService.createFromNote(any()) }
                        verify(exactly = 0) { noteIndexService.deleteByNoteId(any()) }
                    }
                }

                context("NoteIndexStatus.NOT_FOUND - 인덱싱이 되지 않은 노트가 있는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val noteId = UUID.randomUUID()
                    val noteHash = "test-hash-123"

                    val noteExtIdHash = NoteExtIdHash(
                        externalId = noteId.toString(),
                        hash = noteHash,
                        status = NoteStatus.ACTIVE
                    )
                    val testNote = createTestNote(externalId = noteId.toString(), hash = noteHash)

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 1L
                        every {
                            noteService.getAllNoteExtIdHashBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = any()
                            )
                        } returns listOf(noteExtIdHash)
                        every { noteIndexService.getAllByIds(listOf(noteId.toString())) } returns emptyList()
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteIndexService.createFromNote(testNote) } just runs
                    }

                    it("인덱스 생성 작업이 수행된다") {
                        noteIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteIndexService.createFromNote(testNote) }
                    }
                }

                context("NoteIndexStatus.NOT_DELETED - 삭제된 노트의 인덱스가 남아있는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val noteId = UUID.randomUUID()
                    val noteHash = "test-hash-123"

                    val noteExtIdHash = NoteExtIdHash(
                        externalId = noteId.toString(),
                        hash = noteHash,
                        status = NoteStatus.DELETED
                    )
                    val noteIndexIdHash = NoteIndexIdHash(
                        id = noteId.toString(),
                        noteHash = noteHash
                    )

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 1L
                        every {
                            noteService.getAllNoteExtIdHashBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = any()
                            )
                        } returns listOf(noteExtIdHash)
                        every { noteIndexService.getAllByIds(listOf(noteId.toString())) } returns listOf(noteIndexIdHash)
                        every { noteIndexService.deleteByNoteId(noteId) } just runs
                    }

                    it("인덱스 삭제 작업이 수행된다") {
                        noteIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 1) { noteIndexService.deleteByNoteId(noteId) }
                    }
                }

                context("NoteIndexStatus.INVALID - Note와 Index의 해시가 다른 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val noteId = UUID.randomUUID()
                    val noteHash = "new-note-hash"
                    val indexHash = "old-index-hash"

                    val noteExtIdHash = NoteExtIdHash(
                        externalId = noteId.toString(),
                        hash = noteHash,
                        status = NoteStatus.ACTIVE
                    )
                    val noteIndexIdHash = NoteIndexIdHash(
                        id = noteId.toString(),
                        noteHash = indexHash
                    )
                    val testNote = createTestNote(externalId = noteId.toString(), hash = noteHash)

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 1L
                        every {
                            noteService.getAllNoteExtIdHashBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = any()
                            )
                        } returns listOf(noteExtIdHash)
                        every { noteIndexService.getAllByIds(listOf(noteId.toString())) } returns listOf(noteIndexIdHash)
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteIndexService.deleteByNoteId(noteId) } just runs
                        every { noteIndexService.createFromNote(testNote) } just runs
                    }

                    it("인덱스 재생성 작업이 수행된다 (삭제 후 생성)") {
                        noteIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteIndexService.deleteByNoteId(noteId) }
                        verify(exactly = 1) { noteIndexService.createFromNote(testNote) }
                    }
                }

                context("NoteIndexStatus.MULTI_EXISTS - 동일 노트에 대한 인덱스가 여러 개 존재하는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val noteId = UUID.randomUUID()
                    val noteHash = "test-hash-123"

                    val noteExtIdHash = NoteExtIdHash(
                        externalId = noteId.toString(),
                        hash = noteHash,
                        status = NoteStatus.ACTIVE
                    )
                    // 동일 노트에 대한 여러 인덱스 엔트리
                    val noteIndexIdHash1 = NoteIndexIdHash(
                        id = noteId.toString(),
                        noteHash = noteHash
                    )
                    val noteIndexIdHash2 = NoteIndexIdHash(
                        id = noteId.toString(),
                        noteHash = "different-hash"
                    )
                    val testNote = createTestNote(externalId = noteId.toString(), hash = noteHash)

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 1L
                        every {
                            noteService.getAllNoteExtIdHashBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = any()
                            )
                        } returns listOf(noteExtIdHash)
                        every { noteIndexService.getAllByIds(listOf(noteId.toString())) } returns listOf(
                            noteIndexIdHash1,
                            noteIndexIdHash2
                        )
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteIndexService.deleteByNoteId(noteId) } just runs
                        every { noteIndexService.createFromNote(testNote) } just runs
                    }

                    it("인덱스 재생성 작업이 수행된다 (삭제 후 생성)") {
                        noteIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteIndexService.deleteByNoteId(noteId) }
                        verify(exactly = 1) { noteIndexService.createFromNote(testNote) }
                    }
                }

                context("처리할 노트가 없는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 0L
                    }

                    it("배치 처리가 수행되지 않는다") {
                        noteIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 1) { noteService.getCountBeforeModifiedDate(syncRangeStart) }
                        verify(exactly = 0) { noteService.getAllNoteExtIdHashBeforeModifiedDate(any(), any()) }
                    }
                }

                context("배치 크기보다 많은 노트가 있는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val batchSize = 2
                    val totalCount = 5L

                    val noteIds = (1..5).map { UUID.randomUUID() }
                    val noteExtIdHashes = noteIds.map { NoteExtIdHash(it.toString(), "hash-$it", NoteStatus.ACTIVE) }
                    val noteIndexIdHashes = noteIds.map { NoteIndexIdHash(it.toString(), "hash-$it") }

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns totalCount
                        every {
                            noteService.getAllNoteExtIdHashBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = match { it.pageNumber == 0 && it.pageSize == batchSize }
                            )
                        } returns noteExtIdHashes.take(2)
                        every {
                            noteService.getAllNoteExtIdHashBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = match { it.pageNumber == 1 && it.pageSize == batchSize }
                            )
                        } returns noteExtIdHashes.drop(2).take(2)
                        every {
                            noteService.getAllNoteExtIdHashBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = match { it.pageNumber == 2 && it.pageSize == batchSize }
                            )
                        } returns noteExtIdHashes.drop(4)
                        every { noteIndexService.getAllByIds(any()) } answers {
                            val ids = firstArg<List<String>>()
                            noteIndexIdHashes.filter { ids.contains(it.id) }
                        }
                    }

                    it("여러 배치로 나뉘어 처리된다") {
                        noteIndexSyncService.sinkAllNotes(
                            batchSize = batchSize,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 3) { noteService.getAllNoteExtIdHashBeforeModifiedDate(any(), any()) }
                    }
                }

                context("인덱스 생성 중 예외가 발생하는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val noteId = UUID.randomUUID()
                    val noteHash = "test-hash-123"

                    val noteExtIdHash = NoteExtIdHash(
                        externalId = noteId.toString(),
                        hash = noteHash,
                        status = NoteStatus.ACTIVE
                    )
                    val testNote = createTestNote(externalId = noteId.toString(), hash = noteHash)

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 1L
                        every {
                            noteService.getAllNoteExtIdHashBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = any()
                            )
                        } returns listOf(noteExtIdHash)
                        every { noteIndexService.getAllByIds(listOf(noteId.toString())) } returns emptyList()
                        every { noteService.fetchByExternalId(noteId) } returns testNote
                        every { noteIndexService.createFromNote(testNote) } throws RuntimeException("Index creation failed")
                    }

                    it("예외가 로깅되고 프로세스는 계속된다") {
                        // 예외가 발생해도 프로세스가 중단되지 않아야 함
                        noteIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 1) { noteService.fetchByExternalId(noteId) }
                        verify(exactly = 1) { noteIndexService.createFromNote(testNote) }
                    }
                }

                context("삭제된 노트에 대한 인덱스가 없는 정상 상태인 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)
                    val noteId = UUID.randomUUID()
                    val noteHash = "test-hash-123"

                    val noteExtIdHash = NoteExtIdHash(
                        externalId = noteId.toString(),
                        hash = noteHash,
                        status = NoteStatus.DELETED
                    )

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 1L
                        every {
                            noteService.getAllNoteExtIdHashBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = any()
                            )
                        } returns listOf(noteExtIdHash)
                        every { noteIndexService.getAllByIds(listOf(noteId.toString())) } returns emptyList()
                    }

                    it("복구 작업이 수행되지 않는다") {
                        noteIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        verify(exactly = 0) { noteIndexService.createFromNote(any()) }
                        verify(exactly = 0) { noteIndexService.deleteByNoteId(any()) }
                        verify(exactly = 0) { noteService.fetchByExternalId(any()) }
                    }
                }

                context("여러 종류의 상태를 가진 노트가 함께 있는 경우") {
                    val syncRangeStart = LocalDateTime.of(2025, 1, 1, 0, 0)

                    // 정상 노트
                    val normalNoteId = UUID.randomUUID()
                    val normalNoteHash = "normal-hash"
                    val normalNoteExtIdHash = NoteExtIdHash(normalNoteId.toString(), normalNoteHash, NoteStatus.ACTIVE)
                    val normalNoteIndexIdHash = NoteIndexIdHash(normalNoteId.toString(), normalNoteHash)

                    // 인덱스 미생성 노트
                    val notFoundNoteId = UUID.randomUUID()
                    val notFoundNoteHash = "not-found-hash"
                    val notFoundNoteExtIdHash =
                        NoteExtIdHash(notFoundNoteId.toString(), notFoundNoteHash, NoteStatus.ACTIVE)
                    val notFoundTestNote =
                        createTestNote(externalId = notFoundNoteId.toString(), hash = notFoundNoteHash)

                    // 삭제되었으나 인덱스 남은 노트
                    val notDeletedNoteId = UUID.randomUUID()
                    val notDeletedNoteHash = "not-deleted-hash"
                    val notDeletedNoteExtIdHash =
                        NoteExtIdHash(notDeletedNoteId.toString(), notDeletedNoteHash, NoteStatus.DELETED)
                    val notDeletedNoteIndexIdHash = NoteIndexIdHash(notDeletedNoteId.toString(), notDeletedNoteHash)

                    beforeTest {
                        every { noteService.getCountBeforeModifiedDate(syncRangeStart) } returns 3L
                        every {
                            noteService.getAllNoteExtIdHashBeforeModifiedDate(
                                beforeDate = syncRangeStart,
                                pageable = any()
                            )
                        } returns listOf(normalNoteExtIdHash, notFoundNoteExtIdHash, notDeletedNoteExtIdHash)
                        every {
                            noteIndexService.getAllByIds(any())
                        } returns listOf(normalNoteIndexIdHash, notDeletedNoteIndexIdHash)
                        every { noteService.fetchByExternalId(notFoundNoteId) } returns notFoundTestNote
                        every { noteIndexService.createFromNote(notFoundTestNote) } just runs
                        every { noteIndexService.deleteByNoteId(notDeletedNoteId) } just runs
                    }

                    it("각 상태에 맞는 복구 작업이 수행된다") {
                        noteIndexSyncService.sinkAllNotes(
                            batchSize = 1000,
                            syncRangeStart = syncRangeStart
                        )

                        // 정상 노트는 복구 작업 없음
                        verify(exactly = 0) { noteService.fetchByExternalId(normalNoteId) }

                        // 인덱스 미생성 노트는 인덱스 생성
                        verify(exactly = 1) { noteService.fetchByExternalId(notFoundNoteId) }
                        verify(exactly = 1) { noteIndexService.createFromNote(notFoundTestNote) }

                        // 삭제된 노트의 인덱스는 삭제
                        verify(exactly = 1) { noteIndexService.deleteByNoteId(notDeletedNoteId) }
                    }
                }
            }
        }
    }
}

