package com.gabinote.coffeenote.note.mapping.noteDisplayField

import com.gabinote.coffeenote.note.domain.note.NoteDisplayField
import com.gabinote.coffeenote.note.dto.noteDisplayField.controller.NoteDisplayFieldResControllerDto
import com.gabinote.coffeenote.note.dto.noteDisplayField.service.NoteDisplayFieldResServiceDto
import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldCreateReqServiceDto
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldType
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [NoteDisplayFieldMapperImpl::class])
class NoteDisplayFieldMapperTest : MockkTestTemplate() {
    @Autowired
    lateinit var noteDisplayFieldMapper: NoteDisplayFieldMapper

    init {
        describe("[Note] NoteDisplayFieldMapper Test") {
            describe("NoteDisplayFieldMapper.toDisplayField") {
                context("NoteFieldCreateReqServiceDto와 order가 주어지면,") {
                    val dto = NoteFieldCreateReqServiceDto(
                        id = "field-id",
                        name = "Display Field Name",
                        icon = "display-icon",
                        type = TestFieldType,
                        attributes = emptySet(),
                        order = 1,
                        isDisplay = true,
                        values = setOf("value1", "value2", "value3")
                    )
                    val order = 2
                    val expected = NoteDisplayField(
                        name = dto.name,
                        icon = dto.icon,
                        values = dto.values,
                        order = order
                    )
                    it("NoteDisplayField로 변환되어야 한다.") {
                        val result = noteDisplayFieldMapper.toDisplayField(dto, order)
                        result shouldBe expected
                    }
                }
                context("values가 빈 NoteFieldCreateReqServiceDto가 주어지면,") {
                    val dto = NoteFieldCreateReqServiceDto(
                        id = "empty-field-id",
                        name = "Empty Values Field",
                        icon = "empty-icon",
                        type = TestFieldType,
                        attributes = emptySet(),
                        order = 0,
                        isDisplay = true,
                        values = emptySet()
                    )
                    val order = 1
                    val expected = NoteDisplayField(
                        name = dto.name,
                        icon = dto.icon,
                        values = emptySet(),
                        order = order
                    )
                    it("빈 values를 가진 NoteDisplayField로 변환되어야 한다.") {
                        val result = noteDisplayFieldMapper.toDisplayField(dto, order)
                        result shouldBe expected
                    }
                }
               
            }
            describe("NoteDisplayFieldMapper.toResServiceDto") {
                context("NoteDisplayField 엔티티가 주어지면,") {
                    val entity = NoteDisplayField(
                        name = "Display Field Name",
                        icon = "display-icon",
                        values = setOf("value1", "value2", "value3"),
                        order = 1
                    )
                    val expected = NoteDisplayFieldResServiceDto(
                        name = entity.name,
                        icon = entity.icon,
                        values = entity.values,
                        order = entity.order
                    )
                    it("NoteDisplayFieldResServiceDto로 변환되어야 한다.") {
                        val result = noteDisplayFieldMapper.toResServiceDto(entity)
                        result shouldBe expected
                    }
                }


            }
            describe("NoteDisplayFieldMapper.toResControllerDto") {
                context("NoteDisplayFieldResServiceDto가 주어지면,") {
                    val dto = NoteDisplayFieldResServiceDto(
                        name = "Display Field Name",
                        icon = "display-icon",
                        values = setOf("value1", "value2", "value3"),
                        order = 1
                    )
                    val expected = NoteDisplayFieldResControllerDto(
                        name = dto.name,
                        icon = dto.icon,
                        values = dto.values,
                        order = dto.order
                    )
                    it("NoteDisplayFieldResControllerDto로 변환되어야 한다.") {
                        val result = noteDisplayFieldMapper.toResControllerDto(dto)
                        result shouldBe expected
                    }
                }


            }
        }
    }
}
