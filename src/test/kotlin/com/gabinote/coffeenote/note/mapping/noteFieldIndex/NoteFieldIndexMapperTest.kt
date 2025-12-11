package com.gabinote.coffeenote.note.mapping.noteFieldIndex

import com.gabinote.coffeenote.common.util.meiliSearch.helper.data.FacetWithCount
import com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldNameFacetListResControllerDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldNameFacetWithCountResControllerDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldValueFacetListResControllerDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldValueFacetWithCountResControllerDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.service.NoteFieldNameFacetWithCountResServiceDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.service.NoteFieldValueFacetWithCountResServiceDto
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [NoteFieldIndexMapperImpl::class])
class NoteFieldIndexMapperTest : MockkTestTemplate() {
    @Autowired
    lateinit var noteFieldIndexMapper: NoteFieldIndexMapper

    init {
        describe("[Note] NoteFieldIndexMapper Test") {
            describe("NoteFieldIndexMapper.toNoteFieldNameFacetWithCountResServiceDto") {
                context("FacetWithCount가 주어지면,") {
                    val facetWithCount = FacetWithCount(
                        facet = "field-name",
                        count = 10
                    )
                    val expected = NoteFieldNameFacetWithCountResServiceDto(
                        facet = facetWithCount.facet,
                        count = facetWithCount.count
                    )
                    it("NoteFieldNameFacetWithCountResServiceDto로 변환되어야 한다.") {
                        val result = noteFieldIndexMapper.toNoteFieldNameFacetWithCountResServiceDto(facetWithCount)
                        result shouldBe expected
                    }
                }
            }
            describe("NoteFieldIndexMapper.toNoteFieldValueFacetWithCountResServiceDto") {
                context("FacetWithCount가 주어지면,") {
                    val facetWithCount = FacetWithCount(
                        facet = "field-value",
                        count = 20
                    )
                    val expected = NoteFieldValueFacetWithCountResServiceDto(
                        facet = facetWithCount.facet,
                        count = facetWithCount.count
                    )
                    it("NoteFieldValueFacetWithCountResServiceDto로 변환되어야 한다.") {
                        val result = noteFieldIndexMapper.toNoteFieldValueFacetWithCountResServiceDto(facetWithCount)
                        result shouldBe expected
                    }
                }
            }

            describe("NoteFieldIndexMapper.toNoteFieldNameFacetWithCountResControllerDto") {
                context("NoteFieldNameFacetWithCountResServiceDto가 주어지면,") {
                    val serviceDto = NoteFieldNameFacetWithCountResServiceDto(
                        facet = "field-name",
                        count = 15
                    )
                    val expected = NoteFieldNameFacetWithCountResControllerDto(
                        facet = serviceDto.facet,
                        count = serviceDto.count
                    )
                    it("NoteFieldNameFacetWithCountResControllerDto로 변환되어야 한다.") {
                        val result = noteFieldIndexMapper.toNoteFieldNameFacetWithCountResControllerDto(serviceDto)
                        result shouldBe expected
                    }
                }
            }

            describe("NoteFieldIndexMapper.toNoteFieldValueFacetWithCountResControllerDto") {
                context("NoteFieldValueFacetWithCountResServiceDto가 주어지면,") {
                    val serviceDto = NoteFieldValueFacetWithCountResServiceDto(
                        facet = "field-value",
                        count = 25
                    )
                    val expected = NoteFieldValueFacetWithCountResControllerDto(
                        facet = serviceDto.facet,
                        count = serviceDto.count
                    )
                    it("NoteFieldValueFacetWithCountResControllerDto로 변환되어야 한다.") {
                        val result = noteFieldIndexMapper.toNoteFieldValueFacetWithCountResControllerDto(serviceDto)
                        result shouldBe expected
                    }
                }
            }

            describe("NoteFieldIndexMapper.toNoteFieldNameListResControllerDto") {
                context("NoteFieldNameFacetWithCountResControllerDto 리스트가 주어지면,") {
                    val facets = listOf(
                        NoteFieldNameFacetWithCountResControllerDto(facet = "field1", count = 10),
                        NoteFieldNameFacetWithCountResControllerDto(facet = "field2", count = 20),
                        NoteFieldNameFacetWithCountResControllerDto(facet = "field3", count = 30)
                    )
                    val expected =
                        NoteFieldNameFacetListResControllerDto(facets = facets)

                    it("NoteFieldNameFacetListResControllerDto 리스트로 변환되어야 한다.") {
                        val result = noteFieldIndexMapper.toNoteFieldNameListResControllerDto(facets)
                        result shouldBe expected
                    }
                }
            }

            describe("NoteFieldIndexMapper.toNoteFieldValueListResControllerDto") {
                context("NoteFieldValueFacetWithCountResControllerDto 리스트와 fieldName이 주어지면,") {
                    val fieldName = "test-field"
                    val facets = listOf(
                        NoteFieldValueFacetWithCountResControllerDto(facet = "value1", count = 5),
                        NoteFieldValueFacetWithCountResControllerDto(facet = "value2", count = 15),
                        NoteFieldValueFacetWithCountResControllerDto(facet = "value3", count = 25)
                    )
                    val expected =
                        NoteFieldValueFacetListResControllerDto(
                            fieldName = fieldName,
                            facets = facets

                        )
                    it("NoteFieldValueFacetListResControllerDto 리스트로 변환되어야 한다.") {
                        val result = noteFieldIndexMapper.toNoteFieldValueListResControllerDto(facets, fieldName)
                        result shouldBe expected
                    }
                }
            }

        }
    }
}
