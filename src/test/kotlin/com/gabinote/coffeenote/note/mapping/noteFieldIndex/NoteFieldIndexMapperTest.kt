package com.gabinote.coffeenote.note.mapping.noteFieldIndex

import com.gabinote.coffeenote.common.util.meiliSearch.helper.data.FacetWithCount
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
         
        }
    }
}
