package com.gabinote.coffeenote.note.mapping.noteField

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeResControllerDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeResServiceDto
import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapper
import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapperImpl
import com.gabinote.coffeenote.field.mapping.fieldType.FieldTypeMapper
import com.gabinote.coffeenote.field.mapping.fieldType.FieldTypeMapperImpl
import com.gabinote.coffeenote.note.domain.note.NoteField
import com.gabinote.coffeenote.note.dto.noteField.controller.NoteFieldCreateReqControllerDto
import com.gabinote.coffeenote.note.dto.noteField.controller.NoteFieldResControllerDto
import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldCreateReqServiceDto
import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldResServiceDto
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldType
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [NoteFieldMapperImpl::class, AttributeMapperImpl::class, FieldTypeMapperImpl::class])
class NoteFieldMapperTest : MockkTestTemplate() {
    @Autowired
    lateinit var noteFieldMapper: NoteFieldMapper

    @MockkBean
    lateinit var attributeMapper: AttributeMapper

    @MockkBean
    lateinit var fieldTypeMapper: FieldTypeMapper

    init {
        describe("[Note] NoteFieldMapper Test") {
            describe("NoteFieldMapper.toNoteField") {
                context("NoteFieldCreateReqServiceDto와 Attribute 세트가 주어지면,") {
                    val attributeDto = mockk<AttributeCreateReqServiceDto>()
                    val fieldType = TestFieldType
                    val dto = NoteFieldCreateReqServiceDto(
                        id = "note-field-id",
                        name = "Note Field Name",
                        icon = "field-icon",
                        type = fieldType,
                        attributes = setOf(attributeDto),
                        order = 1,
                        isDisplay = true,
                        values = setOf("value1", "value2")
                    )


                    beforeTest {
                        every { fieldTypeMapper.toString(fieldType) } returns fieldType.getKeyString()
                    }

                    val expected = NoteField(
                        id = dto.id,
                        name = dto.name,
                        icon = dto.icon,
                        type = fieldType.getKeyString(),
                        attributes = setOf(), // attribute는 매핑 안됨
                        order = dto.order,
                        isDisplay = dto.isDisplay,
                        values = dto.values
                    )

                    it("NoteField 엔티티로 변환되어야 한다.") {
                        val result = noteFieldMapper.toNoteField(dto)
                        result shouldBe expected
                        verify(exactly = 1) {
                            fieldTypeMapper.toString(fieldType)
                        }
                        verify(exactly = 0) {
                            attributeMapper.toAttribute(attributeDto)
                        }
                    }
                }
            }
            describe("NoteFieldMapper.toResServiceDto") {
                context("NoteField 엔티티가 주어지면,") {
                    val attribute = mockk<Attribute>()
                    val fieldType = TestFieldType
                    val noteField = NoteField(
                        id = "note-field-id",
                        name = "Note Field Name",
                        icon = "field-icon",
                        type = fieldType.getKeyString(),
                        attributes = setOf(attribute),
                        order = 1,
                        isDisplay = true,
                        values = setOf("value1", "value2")
                    )

                    val attributeDto = mockk<AttributeResServiceDto>()

                    beforeTest {
                        every { attributeMapper.toAttributeResServiceDto(attribute) } returns attributeDto
                        every { fieldTypeMapper.toFieldType(fieldType.getKeyString()) } returns fieldType
                    }

                    val expected = NoteFieldResServiceDto(
                        id = noteField.id,
                        name = noteField.name,
                        icon = noteField.icon,
                        type = fieldType,
                        attributes = setOf(attributeDto),
                        order = noteField.order,
                        isDisplay = noteField.isDisplay,
                        values = noteField.values
                    )
                    beforeTest {
                        every { attributeMapper.toAttributeResServiceDto(attribute) } returns attributeDto
                        every { fieldTypeMapper.toFieldType(noteField.type) } returns fieldType
                    }

                    it("NoteFieldResServiceDto로 변환되어야 한다.") {
                        val result = noteFieldMapper.toResServiceDto(noteField)
                        result shouldBe expected
                        verify(exactly = 1) {
                            attributeMapper.toAttributeResServiceDto(attribute)
                            fieldTypeMapper.toFieldType(noteField.type)
                        }
                    }
                }
                context("attributes가 빈 NoteField 엔티티가 주어지면,") {
                    val fieldType = TestFieldType
                    val noteField = NoteField(
                        id = "simple-field-id",
                        name = "Simple Field",
                        icon = "simple-icon",
                        type = fieldType.getKeyString(),
                        attributes = emptySet(),
                        order = 0,
                        isDisplay = false,
                        values = setOf("value1", "value2")
                    )

                    beforeTest {
                        every { fieldTypeMapper.toFieldType(fieldType.getKeyString()) } returns fieldType
                    }

                    val expected = NoteFieldResServiceDto(
                        id = noteField.id,
                        name = noteField.name,
                        icon = noteField.icon,
                        type = fieldType,
                        attributes = emptySet(),
                        order = noteField.order,
                        isDisplay = noteField.isDisplay,
                        values = setOf("value1", "value2")
                    )


                    it("빈 attributes를 가진 NoteFieldResServiceDto로 변환되어야 한다.") {
                        val result = noteFieldMapper.toResServiceDto(noteField)
                        result shouldBe expected
                        verify(exactly = 0) {
                            attributeMapper.toAttributeResServiceDto(any<Attribute>())
                        }
                        verify(exactly = 1) {
                            fieldTypeMapper.toFieldType(fieldType.getKeyString())
                        }
                    }
                }
            }
            describe("NoteFieldMapper.toResControllerDto") {
                context("NoteFieldResServiceDto가 주어지면,") {
                    val attributeDto = mockk<AttributeResServiceDto>()
                    val fieldType = TestFieldType
                    val dto = NoteFieldResServiceDto(
                        id = "note-field-id",
                        name = "Note Field Name",
                        icon = "field-icon",
                        type = fieldType,
                        attributes = setOf(attributeDto),
                        order = 1,
                        isDisplay = true,
                        values = setOf("value1", "value2")
                    )


                    val attributeControllerDto = mockk<AttributeResControllerDto>()

                    beforeTest {
                        every { attributeMapper.toAttributeResControllerDto(attributeDto) } returns attributeControllerDto
                    }

                    val expected = NoteFieldResControllerDto(
                        id = dto.id,
                        name = dto.name,
                        icon = dto.icon,
                        type = fieldType,
                        attributes = setOf(attributeControllerDto),
                        order = dto.order,
                        isDisplay = dto.isDisplay,
                        values = dto.values
                    )

                    it("NoteFieldResControllerDto로 변환되어야 한다.") {
                        val result = noteFieldMapper.toResControllerDto(dto)
                        result shouldBe expected

                        verify(exactly = 1) {
                            attributeMapper.toAttributeResControllerDto(attributeDto)
                        }
                    }
                }
                context("attributes가 빈 NoteFieldResServiceDto가 주어지면,") {
                    val fieldType = TestFieldType
                    val dto = NoteFieldResServiceDto(
                        id = "simple-field-id",
                        name = "Simple Field",
                        icon = "simple-icon",
                        type = fieldType,
                        attributes = emptySet(),
                        order = 0,
                        isDisplay = false,
                        values = emptySet()
                    )

                    val expected = NoteFieldResControllerDto(
                        id = dto.id,
                        name = dto.name,
                        icon = dto.icon,
                        type = fieldType,
                        attributes = emptySet(),
                        order = dto.order,
                        isDisplay = dto.isDisplay,
                        values = dto.values
                    )
                    it("빈 attributes를 가진 NoteFieldResControllerDto로 변환되어야 한다.") {
                        val result = noteFieldMapper.toResControllerDto(dto)
                        result shouldBe expected
                        verify(exactly = 0) {
                            attributeMapper.toAttributeResControllerDto(any<AttributeResServiceDto>())
                        }
                    }
                }
            }

            describe("NoteFieldMapper.toCreateReqServiceDto") {
                context("NoteFieldCreateReqControllerDto가 주어지면,") {
                    val fieldType = TestFieldType
                    val dto = NoteFieldCreateReqControllerDto(
                        id = "note-field-id",
                        name = "Note Field Name",
                        icon = "field-icon",
                        type = fieldType,
                        attributes = emptySet(),
                        order = 1,
                        isDisplay = true,
                        values = setOf("value1", "value2")
                    )

                    val expected = NoteFieldCreateReqServiceDto(
                        id = dto.id,
                        name = dto.name,
                        icon = dto.icon,
                        type = fieldType,
                        attributes = emptySet(),
                        order = dto.order,
                        isDisplay = dto.isDisplay,
                        values = dto.values
                    )

                    it("NoteFieldCreateReqServiceDto로 변환되어야 한다.") {
                        val result = noteFieldMapper.toCreateReqServiceDto(dto)
                        result shouldBe expected
                    }
                }
            }
        }
    }
}
