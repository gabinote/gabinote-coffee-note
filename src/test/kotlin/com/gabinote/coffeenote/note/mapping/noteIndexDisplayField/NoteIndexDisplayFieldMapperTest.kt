package com.gabinote.coffeenote.note.mapping.noteIndexDisplayField

import com.gabinote.coffeenote.note.domain.noteIndex.IndexDisplayField
import com.gabinote.coffeenote.note.dto.noteIndexDisplayField.controller.IndexDisplayFieldResControllerDto
import com.gabinote.coffeenote.note.dto.noteIndexDisplayField.service.IndexDisplayFieldResServiceDto
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [NoteIndexDisplayFieldMapperImpl::class])
class NoteIndexDisplayFieldMapperTest : MockkTestTemplate() {

    @Autowired
    lateinit var noteIndexDisplayFieldMapper: NoteIndexDisplayFieldMapper

    init {
        describe("[Note] NoteIndexDisplayFieldMapper Test") {
            describe("NoteIndexDisplayFieldMapper.toDisplayFieldResServiceDto") {
                context("IndexDisplayField가 주어지면,") {
                    val indexDisplayField = IndexDisplayField(
                        name = "Display Field Name",
                        tag = "display-icon",
                        value = listOf("value1", "value2", "value3"),
                        order = 1
                    )

                    val expected = IndexDisplayFieldResServiceDto(
                        name = indexDisplayField.name,
                        tag = indexDisplayField.tag,
                        value = indexDisplayField.value,
                        order = indexDisplayField.order
                    )

                    it("IndexDisplayFieldResServiceDto로 변환되어야 한다.") {
                        val result = noteIndexDisplayFieldMapper.toDisplayFieldResServiceDto(indexDisplayField)
                        result shouldBe expected
                    }
                }
            }

            describe("NoteIndexDisplayFieldMapper.toDisplayFieldResControllerDto") {

                context("IndexDisplayFieldResServiceDto가 주어지면,") {
                    val serviceDto = IndexDisplayFieldResServiceDto(
                        name = "Display Field Name",
                        tag = "display-icon",
                        value = listOf("value1", "value2", "value3"),
                        order = 1
                    )

                    val expected = IndexDisplayFieldResControllerDto(
                        name = serviceDto.name,
                        tag = serviceDto.tag,
                        value = serviceDto.value,
                        order = serviceDto.order
                    )

                    it("IndexDisplayFieldResControllerDto로 변환되어야 한다.") {
                        val result = noteIndexDisplayFieldMapper.toDisplayFieldResControllerDto(serviceDto)
                        result shouldBe expected
                    }
                }

            }
        }
    }


}