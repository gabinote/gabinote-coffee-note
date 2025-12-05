package com.gabinote.coffeenote.note.service.noteFieldIndex

import com.gabinote.coffeenote.common.util.meiliSearch.helper.data.FacetWithCount
import com.gabinote.coffeenote.note.domain.noteFieldIndex.NoteFieldIndexRepository
import com.gabinote.coffeenote.note.dto.noteFieldIndex.service.NoteFieldNameFacetWithCountResServiceDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.service.NoteFieldValueFacetWithCountResServiceDto
import com.gabinote.coffeenote.note.mapping.noteFieldIndex.NoteFieldIndexMapper
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify


class NoteFieldIndexServiceTest : ServiceTestTemplate() {

    lateinit var noteFieldIndexService: NoteFieldIndexService

    @MockK
    lateinit var noteFieldIndexRepository: NoteFieldIndexRepository

    @MockK
    lateinit var noteFieldIndexMapper: NoteFieldIndexMapper

    init {
        beforeTest {
            clearAllMocks()
            noteFieldIndexService = NoteFieldIndexService(
                noteFieldIndexRepository = noteFieldIndexRepository,
                noteFieldIndexMapper = noteFieldIndexMapper,
            )
        }

        describe("[NoteFieldIndex] NoteFieldIndexService Test") {

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
        }
    }
}

