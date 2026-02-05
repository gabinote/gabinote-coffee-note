package com.gabinote.coffeenote.note.service.noteFieldIndex

import com.gabinote.coffeenote.common.util.meiliSearch.helper.data.FacetWithCount
import com.gabinote.coffeenote.common.util.uuid.UuidSource
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeFactory
import com.gabinote.coffeenote.note.domain.note.Note
import com.gabinote.coffeenote.note.domain.note.NoteDisplayField
import com.gabinote.coffeenote.note.domain.note.NoteField
import com.gabinote.coffeenote.note.domain.noteFieldIndex.NoteFieldIndex
import com.gabinote.coffeenote.note.domain.noteFieldIndex.NoteFieldIndexRepository
import com.gabinote.coffeenote.note.dto.noteFieldIndex.service.NoteFieldNameFacetWithCountResServiceDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.service.NoteFieldValueFacetWithCountResServiceDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.vo.NoteFieldIndexNoteIdHash
import com.gabinote.coffeenote.note.mapping.noteFieldIndex.NoteFieldIndexMapper
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


class NoteFieldIndexServiceTest : ServiceTestTemplate() {

    lateinit var noteFieldIndexService: NoteFieldIndexService

    @MockK
    lateinit var noteFieldIndexRepository: NoteFieldIndexRepository

    @MockK
    lateinit var noteFieldIndexMapper: NoteFieldIndexMapper

    @MockK
    lateinit var uuidSource: UuidSource

    @MockK
    lateinit var fieldTypeFactory: FieldTypeFactory

    @MockK
    lateinit var timeProvider: TestTimeProvider

    init {
        beforeTest {
            clearAllMocks()
            noteFieldIndexService = NoteFieldIndexService(
                noteFieldIndexRepository = noteFieldIndexRepository,
                noteFieldIndexMapper = noteFieldIndexMapper,
                uuidSource = uuidSource,
                fieldTypeFactory = fieldTypeFactory,
                timeProvider = timeProvider
            )
        }

        describe("[Note] NoteFieldIndexService Test") {

            describe("NoteFieldIndexService.searchNoteFieldNameFacets") {
                context("소유자와 쿼리가 주어졌을 때") {
                    val owner = "test-owner"
                    val query = "test"

                    val facetWithCounts = listOf(
                        FacetWithCount(facet = "field1", count = 5),
                        FacetWithCount(facet = "field2", count = 3),
                        FacetWithCount(facet = "field3", count = 1)
                    )

                    val expectedDtos = listOf(
                        NoteFieldNameFacetWithCountResServiceDto(facet = "field1", count = 5),
                        NoteFieldNameFacetWithCountResServiceDto(facet = "field2", count = 3),
                        NoteFieldNameFacetWithCountResServiceDto(facet = "field3", count = 1)
                    )

                    beforeTest {
                        every {
                            noteFieldIndexRepository.searchFieldNameFacets(
                                owner = owner,
                                query = query
                            )
                        } returns facetWithCounts

                        facetWithCounts.forEachIndexed { index, facetWithCount ->
                            every {
                                noteFieldIndexMapper.toNoteFieldNameFacetWithCountResServiceDto(facetWithCount)
                            } returns expectedDtos[index]
                        }
                    }

                    it("필드 이름 Facet 목록을 반환한다") {
                        val result = noteFieldIndexService.searchNoteFieldNameFacets(
                            owner = owner,
                            query = query
                        )

                        result shouldHaveSize 3
                        result[0].facet shouldBe "field1"
                        result[0].count shouldBe 5
                        result[1].facet shouldBe "field2"
                        result[1].count shouldBe 3
                        result[2].facet shouldBe "field3"
                        result[2].count shouldBe 1

                        verify(exactly = 1) {
                            noteFieldIndexRepository.searchFieldNameFacets(
                                owner = owner,
                                query = query
                            )
                        }

                        verify(exactly = 3) {
                            noteFieldIndexMapper.toNoteFieldNameFacetWithCountResServiceDto(any())
                        }
                    }
                }

                context("결과가 없을 때") {
                    val owner = "test-owner"
                    val query = "nonexistent"

                    beforeTest {
                        every {
                            noteFieldIndexRepository.searchFieldNameFacets(
                                owner = owner,
                                query = query
                            )
                        } returns emptyList()
                    }

                    it("빈 리스트를 반환한다") {
                        val result = noteFieldIndexService.searchNoteFieldNameFacets(
                            owner = owner,
                            query = query
                        )

                        result shouldHaveSize 0

                        verify(exactly = 1) {
                            noteFieldIndexRepository.searchFieldNameFacets(
                                owner = owner,
                                query = query
                            )
                        }

                        verify(exactly = 0) {
                            noteFieldIndexMapper.toNoteFieldNameFacetWithCountResServiceDto(any())
                        }
                    }
                }
            }

            describe("NoteFieldIndexService.searchNoteFieldValueFacets") {
                context("소유자, 필드 이름, 쿼리가 주어졌을 때") {
                    val owner = "test-owner"
                    val fieldName = "status"
                    val query = "active"

                    val facetWithCounts = listOf(
                        FacetWithCount(facet = "active", count = 10),
                        FacetWithCount(facet = "inactive", count = 2)
                    )

                    val expectedDtos = listOf(
                        NoteFieldValueFacetWithCountResServiceDto(facet = "active", count = 10),
                        NoteFieldValueFacetWithCountResServiceDto(facet = "inactive", count = 2)
                    )

                    beforeTest {
                        every {
                            noteFieldIndexRepository.searchFieldValueFacets(
                                owner = owner,
                                fieldName = fieldName,
                                query = query
                            )
                        } returns facetWithCounts

                        facetWithCounts.forEachIndexed { index, facetWithCount ->
                            every {
                                noteFieldIndexMapper.toNoteFieldValueFacetWithCountResServiceDto(facetWithCount)
                            } returns expectedDtos[index]
                        }
                    }

                    it("필드 값 Facet 목록을 반환한다") {
                        val result = noteFieldIndexService.searchNoteFieldValueFacets(
                            owner = owner,
                            fieldName = fieldName,
                            query = query
                        )

                        result shouldHaveSize 2
                        result[0].facet shouldBe "active"
                        result[0].count shouldBe 10
                        result[1].facet shouldBe "inactive"
                        result[1].count shouldBe 2

                        verify(exactly = 1) {
                            noteFieldIndexRepository.searchFieldValueFacets(
                                owner = owner,
                                fieldName = fieldName,
                                query = query
                            )
                        }

                        verify(exactly = 2) {
                            noteFieldIndexMapper.toNoteFieldValueFacetWithCountResServiceDto(any())
                        }
                    }
                }

                context("결과가 없을 때") {
                    val owner = "test-owner"
                    val fieldName = "category"
                    val query = "nonexistent"

                    beforeTest {
                        every {
                            noteFieldIndexRepository.searchFieldValueFacets(
                                owner = owner,
                                fieldName = fieldName,
                                query = query
                            )
                        } returns emptyList()
                    }

                    it("빈 리스트를 반환한다") {
                        val result = noteFieldIndexService.searchNoteFieldValueFacets(
                            owner = owner,
                            fieldName = fieldName,
                            query = query
                        )

                        result shouldHaveSize 0

                        verify(exactly = 1) {
                            noteFieldIndexRepository.searchFieldValueFacets(
                                owner = owner,
                                fieldName = fieldName,
                                query = query
                            )
                        }

                        verify(exactly = 0) {
                            noteFieldIndexMapper.toNoteFieldValueFacetWithCountResServiceDto(any())
                        }
                    }
                }

                context("단일 결과만 있을 때") {
                    val owner = "test-owner"
                    val fieldName = "priority"
                    val query = "high"

                    val facetWithCounts = listOf(
                        FacetWithCount(facet = "high", count = 7)
                    )

                    val expectedDtos = listOf(
                        NoteFieldValueFacetWithCountResServiceDto(facet = "high", count = 7)
                    )

                    beforeTest {
                        every {
                            noteFieldIndexRepository.searchFieldValueFacets(
                                owner = owner,
                                fieldName = fieldName,
                                query = query
                            )
                        } returns facetWithCounts

                        every {
                            noteFieldIndexMapper.toNoteFieldValueFacetWithCountResServiceDto(facetWithCounts[0])
                        } returns expectedDtos[0]
                    }

                    it("단일 필드 값 Facet을 반환한다") {
                        val result = noteFieldIndexService.searchNoteFieldValueFacets(
                            owner = owner,
                            fieldName = fieldName,
                            query = query
                        )

                        result shouldHaveSize 1
                        result[0].facet shouldBe "high"
                        result[0].count shouldBe 7

                        verify(exactly = 1) {
                            noteFieldIndexRepository.searchFieldValueFacets(
                                owner = owner,
                                fieldName = fieldName,
                                query = query
                            )
                        }

                        verify(exactly = 1) {
                            noteFieldIndexMapper.toNoteFieldValueFacetWithCountResServiceDto(any())
                        }
                    }
                }
            }

            describe("NoteFieldIndexService.createFromNote") {
                context("올바른 Note 객체가 주어졌을 때") {

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
                        values = setOf("v1", "v2")
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
                    val note = Note(
                        id = ObjectId.get(),
                        externalId = TestUuidSource.UUID_STRING.toString(),
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
                    // 1. convertToNoteFieldIndex
                    // 2. convertToNoteFieldIndexPerField
                    beforeTest {
                        every { fieldTypeFactory.getFieldType(needIndexField.type) } returns TestFieldType

                        every { fieldTypeFactory.getFieldType(excludeIndexField.type) } returns TestFieldTypeExcludeIndexing
                    }

                    // 3. convertToNoteFieldIndexPerValue
                    beforeTest {
                        every { uuidSource.generateUuid() } returns TestUuidSource.UUID_STRING
                        every { timeProvider.zoneOffset() } returns TestTimeProvider.testZoneOffset
                        every { timeProvider.now() } returns TestTimeProvider.testDateTime

                    }
                    val values = needIndexField.values.toList()
                    val noteFieldIndex = NoteFieldIndex(
                        id = TestUuidSource.UUID_STRING.toString(), // '테스트에서만' 동일한 UUID 반환 그러나 실제 운영에서는 랜덤 uuid 사용함에 주의
                        noteId = note.externalId.toString(),
                        name = needIndexField.name,
                        value = values[0],
                        owner = note.owner,
                        synchronizedAt = TestTimeProvider.testEpochSecond,
                        noteHash = "hash",
                        fieldId = "field-included",
                    )

                    val secNoteFieldIndex = NoteFieldIndex(
                        id = TestUuidSource.UUID_STRING.toString(), // '테스트에서만' 동일한 UUID 반환 그러나 실제 운영에서는 랜덤 uuid 사용함에 주의
                        noteId = note.externalId.toString(),
                        name = needIndexField.name,
                        value = values[1],
                        owner = note.owner,
                        synchronizedAt = TestTimeProvider.testEpochSecond,
                        noteHash = "hash",
                        fieldId = "field-included",
                    )

                    val expected = listOf(noteFieldIndex, secNoteFieldIndex)

                    // 4. noteFieldIndexRepository.saveAll
                    beforeTest {
                        every {
                            noteFieldIndexRepository.saveAll(expected)
                        } returns mockk<TaskInfo>()
                    }

                    it("색인 제외 타입을 제외한 노트가 변환되어 저장된다") {
                        noteFieldIndexService.createFromNote(note)
                        verify(exactly = 1) {
                            fieldTypeFactory.getFieldType(needIndexField.type)
                        }
                        verify(exactly = 1) {
                            fieldTypeFactory.getFieldType(excludeIndexField.type)
                        }
                        verify(exactly = 2) {
                            uuidSource.generateUuid()
                            timeProvider.now()
                        }
                        verify(exactly = 1) {
                            noteFieldIndexRepository.saveAll(expected)
                        }
                    }
                }
            }

            describe("NoteFieldIndexService.deleteByNoteExtId") {
                context("유효한 노트 외부 ID가 주어졌을 때") {
                    val noteExtId = TestUuidSource.UUID_STRING

                    beforeTest {
                        every {
                            noteFieldIndexRepository.deleteAllByNoteId(noteExtId.toString())
                        } returns mockk<TaskInfo>()
                    }

                    it("해당 노트의 모든 필드 인덱스를 삭제한다") {
                        noteFieldIndexService.deleteByNoteExtId(noteExtId)

                        verify(exactly = 1) {
                            noteFieldIndexRepository.deleteAllByNoteId(noteExtId.toString())
                        }
                    }
                }
            }

            describe("NoteFieldIndexService.deleteAllByOwner") {
                context("유효한 owner가 주어졌을 때") {
                    val owner = "test-owner"

                    beforeTest {
                        every {
                            noteFieldIndexRepository.deleteAllByOwner(owner)
                        } returns mockk<TaskInfo>()
                    }

                    it("해당 소유자의 모든 노트 필드 인덱스를 삭제한다") {
                        noteFieldIndexService.deleteAllByOwner(owner)

                        verify(exactly = 1) {
                            noteFieldIndexRepository.deleteAllByOwner(owner)
                        }
                    }
                }
            }

            describe("NoteFieldIndexService.getAllByNoteIds") {
                context("노트 ID 목록이 주어졌을 때") {
                    val noteIds = listOf(
                        TestUuidSource.UUID_STRING.toString(),
                        "00000000-0000-0000-0000-000000000002"
                    )

                    val expected = listOf(
                        NoteFieldIndexNoteIdHash(
                            noteId = noteIds[0],
                            value = "some-val",
                            name = "some-name",
                            fieldId = "field_001",
                            id = TestUuidSource.UUID_STRING.toString(),
                        ),
                        NoteFieldIndexNoteIdHash(
                            noteId = noteIds[1],
                            value = "other-val",
                            name = "other-name",
                            fieldId = "field_001",
                            id = "00000000-0000-0000-0000-000000000003",
                        )
                    )

                    beforeTest {
                        every {
                            noteFieldIndexRepository.findAllByNoteIds(noteIds)
                        } returns expected
                    }

                    it("해당 노트 ID들의 NoteFieldIndexNoteIdHash 목록을 반환한다") {
                        val result = noteFieldIndexService.getAllByNoteIds(noteIds)

                        result shouldBe expected

                        verify(exactly = 1) {
                            noteFieldIndexRepository.findAllByNoteIds(noteIds)
                        }
                    }
                }
            }
        }
    }
}
