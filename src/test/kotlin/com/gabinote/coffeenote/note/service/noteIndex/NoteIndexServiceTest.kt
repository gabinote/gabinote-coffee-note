package com.gabinote.coffeenote.note.service.noteIndex

import com.gabinote.coffeenote.common.util.time.TimeProvider
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
import com.gabinote.coffeenote.testSupport.testUtil.time.TestTimeProvider
import com.gabinote.coffeenote.testSupport.testUtil.uuid.TestUuidSource
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
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

    init {
        beforeTest {
            clearAllMocks()
            noteIndexService = NoteIndexService(
                noteIndexRepository = noteIndexRepository,
                noteIndexMapper = noteIndexMapper,
                timeProvider = timeProvider,
            )
        }

        describe("[NoteIndex] NoteIndexService Test") {

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

                    val noteIndex1 = NoteIndex(
                        id = "index-1",
                        externalId = TestUuidSource.UUID_STRING.toString(),
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
                        filters = mapOf("status" to listOf("active"))
                    )

                    val noteIndex2 = NoteIndex(
                        id = "index-2",
                        externalId = TestUuidSource.UUID_STRING.toString(),
                        title = "Test Note 2",
                        owner = owner,
                        createdDate = TestTimeProvider.testEpochSecond,
                        modifiedDate = TestTimeProvider.testEpochSecond,
                        displayFields = emptyList(),
                        filters = emptyMap()
                    )

                    val noteIndexSlice = SliceImpl(
                        listOf(noteIndex1, noteIndex2),
                        pageable,
                        false
                    )

                    val dto1 = NoteIndexResServiceDto(
                        id = "index-1",
                        externalId = noteIndex1.externalId,
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
                        id = "index-2",
                        externalId = noteIndex2.externalId,
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
                        result.content[0].id shouldBe "index-1"
                        result.content[0].title shouldBe "Test Note 1"
                        result.content[1].id shouldBe "index-2"
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

                    val noteIndex = NoteIndex(
                        id = "index-1",
                        externalId = TestUuidSource.UUID_STRING.toString(),
                        title = "Filtered Note",
                        owner = owner,
                        createdDate = TestTimeProvider.testEpochSecond,
                        modifiedDate = TestTimeProvider.testEpochSecond,
                        displayFields = emptyList(),
                        filters = mapOf("status" to listOf("active"))
                    )

                    val noteIndexSlice = SliceImpl(listOf(noteIndex), pageable, false)

                    val dto = NoteIndexResServiceDto(
                        id = "index-1",
                        externalId = noteIndex.externalId,
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
                        result.content[0].id shouldBe "index-1"
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

                    val noteIndex = NoteIndex(
                        id = "index-1",
                        externalId = TestUuidSource.UUID_STRING.toString(),
                        title = "Date Filtered Note",
                        owner = owner,
                        createdDate = TestTimeProvider.testEpochSecond,
                        modifiedDate = TestTimeProvider.testEpochSecond,
                        displayFields = emptyList(),
                        filters = mapOf("status" to listOf("active"))
                    )

                    val noteIndexSlice = SliceImpl(listOf(noteIndex), pageable, false)

                    val dto = NoteIndexResServiceDto(
                        id = "index-1",
                        externalId = noteIndex.externalId,
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
                        result.content[0].id shouldBe "index-1"
                        result.content[0].title shouldBe "Date Filtered Note"

                        verify(exactly = 4) {
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

                    val noteIndex1 = NoteIndex(
                        id = "index-1",
                        externalId = TestUuidSource.UUID_STRING.toString(),
                        title = "Note 1",
                        owner = owner,
                        createdDate = TestTimeProvider.testEpochSecond,
                        modifiedDate = TestTimeProvider.testEpochSecond,
                        displayFields = emptyList(),
                        filters = emptyMap()
                    )

                    val noteIndex2 = NoteIndex(
                        id = "index-2",
                        externalId = TestUuidSource.UUID_STRING.toString(),
                        title = "Note 2",
                        owner = owner,
                        createdDate = TestTimeProvider.testEpochSecond,
                        modifiedDate = TestTimeProvider.testEpochSecond,
                        displayFields = emptyList(),
                        filters = emptyMap()
                    )

                    val noteIndexSlice = SliceImpl(
                        listOf(noteIndex1, noteIndex2),
                        pageable,
                        true
                    )

                    val dto1 = NoteIndexResServiceDto(
                        id = "index-1",
                        externalId = noteIndex1.externalId,
                        title = "Note 1",
                        owner = owner,
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        displayFields = emptyList(),
                        filters = emptyMap()
                    )

                    val dto2 = NoteIndexResServiceDto(
                        id = "index-2",
                        externalId = noteIndex2.externalId,
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
                        result.content[0].id shouldBe "index-1"
                        result.content[1].id shouldBe "index-2"
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
        }
    }
}

