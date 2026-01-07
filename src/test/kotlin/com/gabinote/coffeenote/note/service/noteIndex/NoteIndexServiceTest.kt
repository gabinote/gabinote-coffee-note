package com.gabinote.coffeenote.note.service.noteIndex

import com.gabinote.coffeenote.common.util.time.TimeProvider
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeFactory
import com.gabinote.coffeenote.note.domain.note.Note
import com.gabinote.coffeenote.note.domain.note.NoteDisplayField
import com.gabinote.coffeenote.note.domain.note.NoteField
import com.gabinote.coffeenote.note.domain.noteIndex.IndexDisplayField
import com.gabinote.coffeenote.note.domain.noteIndex.NoteIndex
import com.gabinote.coffeenote.note.domain.noteIndex.NoteIndexRepository
import com.gabinote.coffeenote.note.domain.noteIndex.vo.DateRangeFilter
import com.gabinote.coffeenote.note.dto.noteIndex.domain.NoteFilterCondition
import com.gabinote.coffeenote.note.dto.noteIndex.domain.NoteSearchCondition
import com.gabinote.coffeenote.note.dto.noteIndex.service.NoteIndexResServiceDto
import com.gabinote.coffeenote.note.dto.noteIndexDisplayField.service.IndexDisplayFieldResServiceDto
import com.gabinote.coffeenote.note.mapping.noteIndex.NoteIndexMapper
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldType
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldTypeExcludeIndexing
import com.gabinote.coffeenote.testSupport.testUtil.time.TestTimeProvider
import com.gabinote.coffeenote.testSupport.testUtil.uuid.TestUuidSource
import com.meilisearch.sdk.model.TaskInfo
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.SliceImpl


class NoteIndexServiceTest : ServiceTestTemplate() {

    lateinit var noteIndexService: NoteIndexService

    @MockK
    lateinit var noteIndexRepository: NoteIndexRepository

    @MockK
    lateinit var noteIndexMapper: NoteIndexMapper

    @MockK
    lateinit var timeProvider: TimeProvider

    @MockK
    lateinit var fieldTypeFactory: FieldTypeFactory


    init {
        beforeTest {
            clearAllMocks()
            noteIndexService = NoteIndexService(
                noteIndexRepository = noteIndexRepository,
                noteIndexMapper = noteIndexMapper,
                timeProvider = timeProvider,
                fieldTypeFactory = fieldTypeFactory
            )
        }

        describe("[Note] NoteIndexService Test") {

            describe("NoteIndexService.searchByCondition") {
                context("검색 조건이 주어졌을 때") {
                    val owner = "test-owner"
                    val query = "test query"
                    val highlightTag = "em"
                    val pageable = mockk<Pageable>()

                    val searchCondition = NoteSearchCondition(
                        query = query,
                        owner = owner,
                        pageable = pageable,
                        highlightTag = highlightTag
                    )

                    val uuid1 = TestUuidSource.UUID_STRING.toString()
                    val uuid2 = "00000000-0000-0000-0000-000000000001"

                    val noteIndex1 = NoteIndex(
                        id = uuid1,
                        title = "Test Note 1",
                        owner = owner,
                        createdDate = TestTimeProvider.testEpochSecond,
                        modifiedDate = TestTimeProvider.testEpochSecond,
                        displayFields = listOf(
                            IndexDisplayField(
                                name = "status",
                                tag = "text",
                                value = listOf("active"),
                                order = 0
                            )
                        ),
                        filters = mapOf("status" to listOf("active")),
                        synchronizedAt = TestTimeProvider.testEpochSecond,
                        noteHash = "hash1"
                    )

                    val noteIndex2 = NoteIndex(
                        id = uuid2,
                        title = "Test Note 2",
                        owner = owner,
                        createdDate = TestTimeProvider.testEpochSecond,
                        modifiedDate = TestTimeProvider.testEpochSecond,
                        displayFields = emptyList(),
                        filters = emptyMap(),
                        synchronizedAt = TestTimeProvider.testEpochSecond,
                        noteHash = "hash2"
                    )

                    val noteIndexSlice = SliceImpl(
                        listOf(noteIndex1, noteIndex2),
                        pageable,
                        false
                    )

                    val dto1 = NoteIndexResServiceDto(
                        id = uuid1,
                        title = "Test Note 1",
                        owner = owner,
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        displayFields = listOf(
                            IndexDisplayFieldResServiceDto(
                                name = "status",
                                tag = "text",
                                value = listOf("active"),
                                order = 0
                            )
                        ),
                        filters = mapOf("status" to listOf("active"))
                    )

                    val dto2 = NoteIndexResServiceDto(
                        id = uuid2,
                        title = "Test Note 2",
                        owner = owner,
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        displayFields = emptyList(),
                        filters = emptyMap()
                    )

                    beforeTest {
                        every {
                            noteIndexRepository.searchNotes(
                                owner = owner,
                                query = query,
                                pageable = pageable,
                                highlightTag = highlightTag
                            )
                        } returns noteIndexSlice

                        every {
                            noteIndexMapper.toResServiceDto(noteIndex1)
                        } returns dto1

                        every {
                            noteIndexMapper.toResServiceDto(noteIndex2)
                        } returns dto2
                    }

                    it("검색된 노트 인덱스 리스트를 반환한다") {
                        val result = noteIndexService.searchByCondition(searchCondition)

                        result.content shouldHaveSize 2
                        result.content[0].id shouldBe uuid1
                        result.content[0].title shouldBe "Test Note 1"
                        result.content[1].id shouldBe uuid2
                        result.content[1].title shouldBe "Test Note 2"
                        result.hasNext() shouldBe false

                        verify(exactly = 1) {
                            noteIndexRepository.searchNotes(
                                owner = owner,
                                query = query,
                                pageable = pageable,
                                highlightTag = highlightTag
                            )
                        }

                        verify(exactly = 1) {
                            noteIndexMapper.toResServiceDto(noteIndex1)
                        }

                        verify(exactly = 1) {
                            noteIndexMapper.toResServiceDto(noteIndex2)
                        }
                    }
                }

                context("검색 결과가 없을 때") {
                    val owner = "test-owner"
                    val query = "no-result"
                    val highlightTag = "em"
                    val pageable = mockk<Pageable>()

                    val searchCondition = NoteSearchCondition(
                        query = query,
                        owner = owner,
                        pageable = pageable,
                        highlightTag = highlightTag
                    )

                    val emptySlice = SliceImpl<NoteIndex>(emptyList(), pageable, false)

                    beforeTest {
                        every {
                            noteIndexRepository.searchNotes(
                                owner = owner,
                                query = query,
                                pageable = pageable,
                                highlightTag = highlightTag
                            )
                        } returns emptySlice
                    }

                    it("빈 리스트를 반환한다") {
                        val result = noteIndexService.searchByCondition(searchCondition)

                        result.content shouldHaveSize 0
                        result.hasNext() shouldBe false

                        verify(exactly = 1) {
                            noteIndexRepository.searchNotes(
                                owner = owner,
                                query = query,
                                pageable = pageable,
                                highlightTag = highlightTag
                            )
                        }

                        verify(exactly = 0) {
                            noteIndexMapper.toResServiceDto(any())
                        }
                    }
                }
            }

            describe("NoteIndexService.filterByCondition") {
                context("필터 조건만 주어졌을 때") {
                    val owner = "test-owner"
                    val highlightTag = "em"
                    val pageable = mockk<Pageable>()
                    val fieldOptions = mapOf(
                        "status" to listOf("active", "pending")
                    )

                    val filterCondition = NoteFilterCondition(
                        fieldOptions = fieldOptions,
                        owner = owner,
                        pageable = pageable,
                        highlightTag = highlightTag
                    )

                    val uuid1 = TestUuidSource.UUID_STRING.toString()

                    val noteIndex = NoteIndex(
                        id = uuid1,
                        title = "Filtered Note",
                        owner = owner,
                        createdDate = TestTimeProvider.testEpochSecond,
                        modifiedDate = TestTimeProvider.testEpochSecond,
                        displayFields = emptyList(),
                        filters = mapOf("status" to listOf("active")),
                        synchronizedAt = TestTimeProvider.testEpochSecond,
                        noteHash = "hash1"
                    )

                    val noteIndexSlice = SliceImpl(listOf(noteIndex), pageable, false)

                    val dto = NoteIndexResServiceDto(
                        id = uuid1,
                        title = "Filtered Note",
                        owner = owner,
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        displayFields = emptyList(),
                        filters = mapOf("status" to listOf("active"))
                    )

                    beforeTest {
                        every {
                            noteIndexRepository.searchNotesWithFilter(
                                owner = owner,
                                filters = fieldOptions,
                                pageable = pageable,
                                highlightTag = highlightTag,
                                createdDateFilter = DateRangeFilter(null, null),
                                modifiedDateFilter = DateRangeFilter(null, null)
                            )
                        } returns noteIndexSlice

                        every {
                            noteIndexMapper.toResServiceDto(noteIndex)
                        } returns dto
                    }

                    it("필터링된 노트 인덱스 리스트를 반환한다") {
                        val result = noteIndexService.filterByCondition(filterCondition)

                        result.content shouldHaveSize 1
                        result.content[0].id shouldBe uuid1
                        result.content[0].title shouldBe "Filtered Note"

                        verify(exactly = 1) {
                            noteIndexRepository.searchNotesWithFilter(
                                owner = owner,
                                filters = fieldOptions,
                                pageable = pageable,
                                highlightTag = highlightTag,
                                createdDateFilter = DateRangeFilter(null, null),
                                modifiedDateFilter = DateRangeFilter(null, null)
                            )
                        }

                        verify(exactly = 1) {
                            noteIndexMapper.toResServiceDto(noteIndex)
                        }
                    }
                }

                context("날짜 범위 필터가 포함된 조건이 주어졌을 때") {
                    val owner = "test-owner"
                    val highlightTag = "em"
                    val pageable = mockk<Pageable>()
                    val fieldOptions = mapOf("status" to listOf("active"))
                    val createdDateStart = TestTimeProvider.testDateTime.minusDays(10)
                    val createdDateEnd = TestTimeProvider.testDateTime.plusDays(10)
                    val modifiedDateStart = TestTimeProvider.testDateTime.minusDays(5)
                    val modifiedDateEnd = TestTimeProvider.testDateTime.plusDays(5)
                    val zoneOffset = TestTimeProvider().zoneOffset()

                    val filterCondition = NoteFilterCondition(
                        fieldOptions = fieldOptions,
                        owner = owner,
                        createdDateStart = createdDateStart,
                        createdDateEnd = createdDateEnd,
                        modifiedDateStart = modifiedDateStart,
                        modifiedDateEnd = modifiedDateEnd,
                        pageable = pageable,
                        highlightTag = highlightTag
                    )

                    val uuid1 = TestUuidSource.UUID_STRING.toString()

                    val noteIndex = NoteIndex(
                        id = uuid1,
                        title = "Date Filtered Note",
                        owner = owner,
                        createdDate = TestTimeProvider.testEpochSecond,
                        modifiedDate = TestTimeProvider.testEpochSecond,
                        displayFields = emptyList(),
                        filters = mapOf("status" to listOf("active")),
                        synchronizedAt = TestTimeProvider.testEpochSecond,
                        noteHash = "hash1"
                    )

                    val noteIndexSlice = SliceImpl(listOf(noteIndex), pageable, false)

                    val dto = NoteIndexResServiceDto(
                        id = uuid1,
                        title = "Date Filtered Note",
                        owner = owner,
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        displayFields = emptyList(),
                        filters = mapOf("status" to listOf("active"))
                    )

                    beforeTest {
                        every {
                            timeProvider.zoneOffset()
                        } returns zoneOffset

                        every {
                            noteIndexRepository.searchNotesWithFilter(
                                owner = owner,
                                filters = fieldOptions,
                                pageable = pageable,
                                highlightTag = highlightTag,
                                createdDateFilter = DateRangeFilter(
                                    startDate = createdDateStart.toEpochSecond(zoneOffset),
                                    endDate = createdDateEnd.toEpochSecond(zoneOffset)
                                ),
                                modifiedDateFilter = DateRangeFilter(
                                    startDate = modifiedDateStart.toEpochSecond(zoneOffset),
                                    endDate = modifiedDateEnd.toEpochSecond(zoneOffset)
                                )
                            )
                        } returns noteIndexSlice

                        every {
                            noteIndexMapper.toResServiceDto(noteIndex)
                        } returns dto
                    }

                    it("날짜 범위로 필터링된 노트 인덱스 리스트를 반환한다") {
                        val result = noteIndexService.filterByCondition(filterCondition)

                        result.content shouldHaveSize 1
                        result.content[0].id shouldBe uuid1
                        result.content[0].title shouldBe "Date Filtered Note"

                        verify(exactly = 1) {
                            timeProvider.zoneOffset()
                        }

                        verify(exactly = 1) {
                            noteIndexRepository.searchNotesWithFilter(
                                owner = owner,
                                filters = fieldOptions,
                                pageable = pageable,
                                highlightTag = highlightTag,
                                createdDateFilter = DateRangeFilter(
                                    startDate = createdDateStart.toEpochSecond(zoneOffset),
                                    endDate = createdDateEnd.toEpochSecond(zoneOffset)
                                ),
                                modifiedDateFilter = DateRangeFilter(
                                    startDate = modifiedDateStart.toEpochSecond(zoneOffset),
                                    endDate = modifiedDateEnd.toEpochSecond(zoneOffset)
                                )
                            )
                        }

                        verify(exactly = 1) {
                            noteIndexMapper.toResServiceDto(noteIndex)
                        }
                    }
                }

                context("빈 필터 조건이 주어졌을 때") {
                    val owner = "test-owner"
                    val highlightTag = "em"
                    val pageable = mockk<Pageable>()
                    val fieldOptions = emptyMap<String, List<String>>()

                    val filterCondition = NoteFilterCondition(
                        fieldOptions = fieldOptions,
                        owner = owner,
                        pageable = pageable,
                        highlightTag = highlightTag
                    )

                    val uuid1 = TestUuidSource.UUID_STRING.toString()
                    val uuid2 = "00000000-0000-0000-0000-000000000001"

                    val noteIndex1 = NoteIndex(
                        id = uuid1,
                        title = "Note 1",
                        owner = owner,
                        createdDate = TestTimeProvider.testEpochSecond,
                        modifiedDate = TestTimeProvider.testEpochSecond,
                        displayFields = emptyList(),
                        filters = emptyMap(),
                        synchronizedAt = TestTimeProvider.testEpochSecond,
                        noteHash = "hash1"
                    )

                    val noteIndex2 = NoteIndex(
                        id = uuid2,
                        title = "Note 2",
                        owner = owner,
                        createdDate = TestTimeProvider.testEpochSecond,
                        modifiedDate = TestTimeProvider.testEpochSecond,
                        displayFields = emptyList(),
                        filters = emptyMap(),
                        synchronizedAt = TestTimeProvider.testEpochSecond,
                        noteHash = "hash2"
                    )

                    val noteIndexSlice = SliceImpl(
                        listOf(noteIndex1, noteIndex2),
                        pageable,
                        true
                    )

                    val dto1 = NoteIndexResServiceDto(
                        id = uuid1,
                        title = "Note 1",
                        owner = owner,
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        displayFields = emptyList(),
                        filters = emptyMap()
                    )

                    val dto2 = NoteIndexResServiceDto(
                        id = uuid2,
                        title = "Note 2",
                        owner = owner,
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        displayFields = emptyList(),
                        filters = emptyMap()
                    )

                    beforeTest {
                        every {
                            noteIndexRepository.searchNotesWithFilter(
                                owner = owner,
                                filters = fieldOptions,
                                pageable = pageable,
                                highlightTag = highlightTag,
                                createdDateFilter = DateRangeFilter(null, null),
                                modifiedDateFilter = DateRangeFilter(null, null)
                            )
                        } returns noteIndexSlice

                        every {
                            noteIndexMapper.toResServiceDto(noteIndex1)
                        } returns dto1

                        every {
                            noteIndexMapper.toResServiceDto(noteIndex2)
                        } returns dto2
                    }

                    it("모든 노트 인덱스를 반환한다") {
                        val result = noteIndexService.filterByCondition(filterCondition)

                        result.content shouldHaveSize 2
                        result.content[0].id shouldBe uuid1
                        result.content[1].id shouldBe uuid2
                        result.hasNext() shouldBe true

                        verify(exactly = 1) {
                            noteIndexRepository.searchNotesWithFilter(
                                owner = owner,
                                filters = fieldOptions,
                                pageable = pageable,
                                highlightTag = highlightTag,
                                createdDateFilter = DateRangeFilter(null, null),
                                modifiedDateFilter = DateRangeFilter(null, null)
                            )
                        }

                        verify(exactly = 2) {
                            noteIndexMapper.toResServiceDto(any())
                        }
                    }
                }
            }

            describe("NoteIndexService.createFromNote") {
                context("올바른 노트가 주어졌을 때") {
                    val zoneOffset = TestTimeProvider.testZoneOffset
                    val displayField = NoteDisplayField(
                        name = "displayField",
                        icon = "icon",
                        values = setOf("v1"),
                        order = 0
                    )

                    val needIndexField = NoteField(
                        id = "field-included",
                        name = "Included Field",
                        icon = "icon",
                        type = TestFieldType.key.name,
                        attributes = emptySet(),
                        order = 0,
                        isDisplay = true,
                        values = setOf("v1")
                    )

                    val excludeIndexField = NoteField(
                        id = "field-excluded",
                        name = "Excluded Field",
                        icon = "icon",
                        type = TestFieldTypeExcludeIndexing.key.name,
                        attributes = emptySet(),
                        order = 1,
                        isDisplay = true,
                        values = setOf("v2")
                    )

                    val noteExternalId = TestUuidSource.UUID_STRING.toString()

                    val note = Note(
                        id = ObjectId.get(),
                        externalId = noteExternalId,
                        title = "Create Note",
                        thumbnail = null,
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        fields = listOf(
                            needIndexField,
                            excludeIndexField
                        ),
                        displayFields = listOf(displayField),
                        isOpen = false,
                        owner = "test-owner",
                        hash = "hash"
                    )

                    // 1. convertToDisplayFields
                    val convertedDisplayField = IndexDisplayField(
                        name = displayField.name,
                        tag = displayField.icon,
                        value = displayField.values.toList(),
                        order = displayField.order
                    )

                    //2. convertToFilters
                    beforeTest {
                        every { fieldTypeFactory.getFieldType(needIndexField.type) } returns TestFieldType

                        every { fieldTypeFactory.getFieldType(excludeIndexField.type) } returns TestFieldTypeExcludeIndexing
                    }

                    val expectedFilters = mapOf(
                        "Included Field" to listOf("v1")
                    )

                    //3. noteIndex 생성
                    beforeTest {
                        every { timeProvider.zoneOffset() } returns zoneOffset
                        every { timeProvider.now() } returns TestTimeProvider.testDateTime
                    }

                    val expectedNoteIndex = NoteIndex(
                        id = noteExternalId,
                        title = note.title,
                        owner = note.owner,
                        createdDate = TestTimeProvider.testEpochSecond,
                        modifiedDate = TestTimeProvider.testEpochSecond,
                        displayFields = listOf(
                            convertedDisplayField,
                        ),
                        filters = expectedFilters,
                        synchronizedAt = TestTimeProvider.testEpochSecond,
                        noteHash = "hash"
                    )

                    //4. 저장
                    beforeTest {
                        every {
                            noteIndexRepository.save(expectedNoteIndex)
                        } returns mockk<TaskInfo>()
                    }

                    it("인덱스가 저장되고, 제외 타입은 필터에 포함되지 않아야 한다") {

                        noteIndexService.createFromNote(note)
                        verify(exactly = 1) {
                            fieldTypeFactory.getFieldType(needIndexField.type)
                            fieldTypeFactory.getFieldType(excludeIndexField.type)
                            timeProvider.zoneOffset()
                            timeProvider.now()
                            noteIndexRepository.save(expectedNoteIndex)
                        }

                    }
                }
            }

            describe("NoteIndexService.deleteByNoteId") {
                context("유효한 노트 ID가 주어졌을 때") {
                    val noteId = TestUuidSource.UUID_STRING

                    beforeTest {
                        every {
                            noteIndexRepository.delete(noteId.toString())
                        } returns mockk<TaskInfo>()
                    }

                    it("해당 노트의 인덱스를 삭제한다") {
                        noteIndexService.deleteByNoteId(noteId)

                        verify(exactly = 1) {
                            noteIndexRepository.delete(noteId.toString())
                        }
                    }
                }
            }

            describe("NoteIndexService.deleteAllByOwner") {
                context("유효한 owner가 주어졌을 때") {
                    val owner = "test-owner"

                    beforeTest {
                        every {
                            noteIndexRepository.deleteAllByOwner(owner)
                        } returns mockk<TaskInfo>()
                    }

                    it("해당 소유자의 모든 노트 인덱스를 삭제한다") {
                        noteIndexService.deleteAllByOwner(owner)

                        verify(exactly = 1) {
                            noteIndexRepository.deleteAllByOwner(owner)
                        }
                    }
                }
            }

        }
    }
}
