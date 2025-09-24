package com.gabinote.coffeenote.template.mapping.templateField

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeResControllerDto
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeUpdateReqControllerDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeResServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeUpdateReqServiceDto
import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapper
import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapperImpl
import com.gabinote.coffeenote.field.mapping.fieldType.FieldTypeMapper
import com.gabinote.coffeenote.field.mapping.fieldType.FieldTypeMapperImpl
import com.gabinote.coffeenote.template.domain.templateField.TemplateField
import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldCreateReqControllerDto
import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldPatchReqControllerDto
import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldResControllerDto
import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldCreateReqServiceDto
import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldPatchReqServiceDto
import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldResServiceDto
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [TemplateFieldMapperImpl::class, AttributeMapperImpl::class, FieldTypeMapperImpl::class])
class TemplateFieldMapperTest : MockkTestTemplate() {

    @Autowired
    lateinit var templateFieldMapper: TemplateFieldMapper

    @MockkBean
    lateinit var attributeMapper: AttributeMapper

    @MockkBean
    lateinit var fieldTypeMapper: FieldTypeMapper

    init {
        describe("[Template] TemplateFieldMapper Test") {

            describe("TemplateFieldMapper.toResServiceDto") {
                context("TemplateField 엔티티가 주어지면,") {
                    val attribute = mockk<Attribute>()
                    val templateField = TemplateField(
                        id = "template-field-id",
                        name = "Template Field Name",
                        icon = "field-icon",
                        type = "text",
                        attributes = setOf(attribute),
                        order = 1,
                        isDisplay = true
                    )
                    val expectedAttribute = mockk<AttributeResServiceDto>()
                    val fieldType = mockk<FieldType>()
                    beforeTest {
                        every { fieldTypeMapper.toFieldType(templateField.type) } returns fieldType
                    }
                    val expected = TemplateFieldResServiceDto(
                        id = templateField.id,
                        name = templateField.name,
                        icon = templateField.icon,
                        type = fieldType,
                        order = templateField.order,
                        isDisplay = templateField.isDisplay,
                        attributes = setOf(expectedAttribute)
                    )

                    beforeTest {
                        every { attributeMapper.toAttributeResServiceDto(attribute) } returns expectedAttribute
                    }

                    it("TemplateFieldResServiceDto로 변환되어야 한다.") {
                        val result = templateFieldMapper.toResServiceDto(templateField)
                        result shouldBe expected

                        verify(exactly = 1) {
                            attributeMapper.toAttributeResServiceDto(attribute)
                            fieldTypeMapper.toFieldType(templateField.type)
                        }
                    }
                }
                context("attributes가 빈 TemplateField 엔티티가 주어지면,") {
                    val templateField = TemplateField(
                        id = "template-field-id",
                        name = "Simple Field",
                        icon = "simple-icon",
                        type = "text",
                        attributes = emptySet(),
                        order = 0,
                        isDisplay = true
                    )
                    val fieldType = mockk<FieldType>()
                    beforeTest {
                        every { fieldTypeMapper.toFieldType(templateField.type) } returns fieldType
                    }
                    val expected = TemplateFieldResServiceDto(
                        id = templateField.id,
                        name = templateField.name,
                        icon = templateField.icon,
                        type = fieldType,
                        order = templateField.order,
                        isDisplay = templateField.isDisplay,
                        attributes = emptySet()
                    )

                    it("빈 attributes를 가진 TemplateFieldResServiceDto로 변환되어야 한다.") {
                        val result = templateFieldMapper.toResServiceDto(templateField)
                        result shouldBe expected

                        verify(exactly = 0) {
                            attributeMapper.toAttributeResServiceDto(any<Attribute>())
                        }

                        verify(exactly = 1) {
                            fieldTypeMapper.toFieldType(templateField.type)
                        }
                    }
                }

                describe("TemplateFieldMapper.toResControllerDto") {
                    context("TemplateFieldResServiceDto가 주어지면,") {
                        val attributeDto = mockk<AttributeResServiceDto>()
                        val dto = TemplateFieldResServiceDto(
                            id = "template-field-id",
                            name = "Template Field Name",
                            icon = "field-icon",
                            type = mockk<FieldType>(),
                            order = 1,
                            isDisplay = true,
                            attributes = setOf(attributeDto)
                        )
                        val expectedAttribute = mockk<AttributeResControllerDto>()
                        val expected = TemplateFieldResControllerDto(
                            id = dto.id,
                            name = dto.name,
                            icon = dto.icon,
                            type = dto.type,
                            order = dto.order,
                            isDisplay = dto.isDisplay,
                            attributes = setOf(expectedAttribute)
                        )

                        beforeTest {
                            every { attributeMapper.toAttributeResControllerDto(attributeDto) } returns expectedAttribute
                        }

                        it("TemplateFieldResControllerDto로 변환되어야 한다.") {
                            val result = templateFieldMapper.toResControllerDto(dto)
                            result shouldBe expected

                            verify(exactly = 1) { attributeMapper.toAttributeResControllerDto(attributeDto) }
                        }
                    }
                    context("attributes가 빈 TemplateFieldResServiceDto가 주어지면,") {
                        val dto = TemplateFieldResServiceDto(
                            id = "template-field-id",
                            name = "Simple Field",
                            icon = "simple-icon",
                            type = mockk<FieldType>(),
                            order = 0,
                            isDisplay = true,
                            attributes = emptySet()
                        )
                        val expected = TemplateFieldResControllerDto(
                            id = dto.id,
                            name = dto.name,
                            icon = dto.icon,
                            type = dto.type,
                            order = dto.order,
                            isDisplay = dto.isDisplay,
                            attributes = emptySet()
                        )

                        it("빈 attributes를 가진 TemplateFieldResControllerDto로 변환되어야 한다.") {
                            val result = templateFieldMapper.toResControllerDto(dto)
                            result shouldBe expected

                            verify(exactly = 0) { attributeMapper.toAttributeResControllerDto(any<AttributeResServiceDto>()) }
                        }
                    }
                }

                describe("TemplateFieldMapper.toCreateReqServiceDto") {
                    context("올바른 TemplateFieldCreateReqControllerDto가 주어지면,") {
                        val attributeDto = mockk<AttributeCreateReqControllerDto>()
                        val dto = TemplateFieldCreateReqControllerDto(
                            id = "template-field-id",
                            name = "Template Field Name",
                            icon = "field-icon",
                            type = mockk<FieldType>(),
                            order = 1,
                            isDisplay = true,
                            attributes = setOf(attributeDto)
                        )
                        val expectedAttribute = mockk<AttributeCreateReqServiceDto>()
                        val expected = TemplateFieldCreateReqServiceDto(
                            id = dto.id,
                            name = dto.name,
                            icon = dto.icon,
                            type = dto.type,
                            order = dto.order,
                            isDisplay = dto.isDisplay,
                            attributes = setOf(expectedAttribute)
                        )

                        beforeTest {
                            every { attributeMapper.toAttributeCreateReqServiceDto(attributeDto) } returns expectedAttribute
                        }

                        it("TemplateFieldCreateReqServiceDto로 변환되어야 한다.") {
                            val result = templateFieldMapper.toCreateReqServiceDto(dto)
                            result shouldBe expected

                            verify(exactly = 1) { attributeMapper.toAttributeCreateReqServiceDto(attributeDto) }
                        }
                    }
                    context("attributes가 빈 TemplateFieldCreateReqControllerDto가 주어지면,") {
                        val dto = TemplateFieldCreateReqControllerDto(
                            id = "template-field-id",
                            name = "Simple Field",
                            icon = "simple-icon",
                            type = mockk<FieldType>(),
                            order = 0,
                            isDisplay = false,
                            attributes = emptySet()
                        )
                        val expected = TemplateFieldCreateReqServiceDto(
                            id = dto.id,
                            name = dto.name,
                            icon = dto.icon,
                            type = dto.type,
                            order = dto.order,
                            isDisplay = dto.isDisplay,
                            attributes = emptySet()
                        )

                        it("빈 attributes를 가진 TemplateFieldCreateReqServiceDto로 변환되어야 한다.") {
                            val result = templateFieldMapper.toCreateReqServiceDto(dto)
                            result shouldBe expected

                            verify(exactly = 0) { attributeMapper.toAttributeCreateReqServiceDto(any<AttributeCreateReqControllerDto>()) }
                        }
                    }
                }

                describe("TemplateFieldMapper.toPatchReqServiceDto") {
                    context("모든 필드가 설정된 TemplateFieldPatchReqControllerDto가 주어지면,") {
                        val attributeDto = mockk<AttributeUpdateReqControllerDto>()
                        val dto = TemplateFieldPatchReqControllerDto(
                            id = "template-field-id",
                            name = "Updated Template Field",
                            icon = "updated-icon",
                            type = mockk<FieldType>(),
                            order = 2,
                            isDisplay = false,
                            attributes = setOf(attributeDto)
                        )
                        val expectedAttribute = mockk<AttributeUpdateReqServiceDto>()
                        val expected = TemplateFieldPatchReqServiceDto(
                            id = dto.id,
                            name = dto.name,
                            icon = dto.icon,
                            type = dto.type,
                            order = dto.order,
                            isDisplay = dto.isDisplay,
                            attributes = setOf(expectedAttribute)
                        )

                        beforeTest {
                            every { attributeMapper.toAttributeUpdateReqServiceDto(attributeDto) } returns expectedAttribute
                        }

                        it("TemplateFieldPatchReqServiceDto로 변환되어야 한다.") {
                            val result = templateFieldMapper.toPatchReqServiceDto(dto)
                            result shouldBe expected

                            verify(exactly = 1) { attributeMapper.toAttributeUpdateReqServiceDto(attributeDto) }
                        }
                    }
                    context("일부 필드만 설정된 TemplateFieldPatchReqControllerDto가 주어지면,") {
                        val dto = TemplateFieldPatchReqControllerDto(
                            id = "template-field-id",
                            name = "Updated Name Only",
                            icon = null,
                            type = null,
                            order = null,
                            isDisplay = null,
                            attributes = emptySet()
                        )
                        val expected = TemplateFieldPatchReqServiceDto(
                            id = dto.id,
                            name = dto.name,
                            icon = null,
                            type = null,
                            order = null,
                            isDisplay = null,
                            attributes = emptySet()
                        )

                        it("null 값이 보존된 TemplateFieldPatchReqServiceDto로 변환되어야 한다.") {
                            val result = templateFieldMapper.toPatchReqServiceDto(dto)
                            result shouldBe expected

                            verify(exactly = 0) { attributeMapper.toAttributeUpdateReqServiceDto(any<AttributeUpdateReqControllerDto>()) }
                        }
                    }
                }

                describe("TemplateFieldMapper.toTemplateField") {
                    context("올바른 TemplateFieldCreateReqServiceDto가 주어지면,") {
                        val dto = TemplateFieldCreateReqServiceDto(
                            id = "template-field-id",
                            name = "Template Field Name",
                            icon = "field-icon",
                            type = mockk<FieldType>(),
                            order = 1,
                            isDisplay = true,
                            attributes = setOf()
                        )

                        beforeTest {
                            every { fieldTypeMapper.toString(dto.type) } returns "text"
                        }

                        val expected = TemplateField(
                            id = dto.id,
                            name = dto.name,
                            icon = dto.icon,
                            type = "text",
                            order = dto.order,
                            isDisplay = dto.isDisplay,
                            attributes = setOf()
                        )


                        it("TemplateField 엔티티로 변환되어야 한다.") {
                            val result = templateFieldMapper.toTemplateField(dto)
                            result shouldBe expected

                            verify(exactly = 1) {
                                fieldTypeMapper.toString(dto.type)
                            }
                        }
                    }
                }

                describe("TemplateFieldMapper.patchFromDto") {
                    context("모든 값이 들어있는 TemplateFieldPatchReqServiceDto와 기존 TemplateField 엔티티가 주어지면,") {
                        val dto = TemplateFieldPatchReqServiceDto(
                            id = "dto-id", // 무시될 값
                            name = "Updated Template Field",
                            icon = "updated-icon",
                            type = mockk<FieldType>(),
                            order = 2,
                            isDisplay = false,
                            attributes = setOf() // 무시될 값
                        )
                        val existingEntity = TemplateField(
                            id = "existing-id",
                            name = "Original Field",
                            icon = "original-icon",
                            type = "original-type",
                            order = 1,
                            isDisplay = true,
                            attributes = setOf(mockk<Attribute>())
                        )
                        beforeTest {
                            every { fieldTypeMapper.toString(dto.type!!) } returns "updated-type"
                        }
                        val expected = TemplateField(
                            id = existingEntity.id, // id는 무시됨
                            name = dto.name!!,
                            icon = dto.icon!!,
                            type = "updated-type",
                            order = dto.order!!,
                            isDisplay = dto.isDisplay!!,
                            attributes = existingEntity.attributes // attributes는 무시됨
                        )

                        it("기존 엔티티의 지정된 필드들이 업데이트되어야 한다.") {
                            val result = templateFieldMapper.patchFromDto(dto, existingEntity)
                            result shouldBe expected
                        }
                    }
                    context("name만 들어있는 TemplateFieldPatchReqServiceDto가 주어지면,") {
                        val dto = TemplateFieldPatchReqServiceDto(
                            id = "dto-id", // 무시될 값
                            name = "Updated Name Only",
                            icon = null,
                            type = null,
                            order = null,
                            isDisplay = null,
                            attributes = setOf() // 무시될 값
                        )
                        val existingEntity = TemplateField(
                            id = "existing-id",
                            name = "Original Field",
                            icon = "original-icon",
                            type = "original-type",
                            order = 1,
                            isDisplay = true,
                            attributes = setOf(mockk<Attribute>())
                        )
                        val expected = TemplateField(
                            id = existingEntity.id, // id는 변경되지 않음
                            name = dto.name!!, // 업데이트됨
                            icon = existingEntity.icon, // null이므로 변경되지 않음
                            type = existingEntity.type, // null이므로 변경되지 않음
                            order = existingEntity.order, // null이므로 변경되지 않음
                            isDisplay = existingEntity.isDisplay, // null이므로 변경되지 않음
                            attributes = existingEntity.attributes // 무시됨
                        )

                        it("기존 엔티티의 name 필드만 업데이트되어야 한다.") {
                            val result = templateFieldMapper.patchFromDto(dto, existingEntity)
                            result shouldBe expected
                        }
                    }
                    context("isDisplay만 들어있는 TemplateFieldPatchReqServiceDto가 주어지면,") {
                        val dto = TemplateFieldPatchReqServiceDto(
                            id = "dto-id", // 무시될 값
                            name = null,
                            icon = null,
                            type = null,
                            order = null,
                            isDisplay = false,
                            attributes = setOf() // 무시될 값
                        )
                        val existingEntity = TemplateField(
                            id = "existing-id",
                            name = "Original Field",
                            icon = "original-icon",
                            type = "original-type",
                            order = 1,
                            isDisplay = true,
                            attributes = setOf(mockk<Attribute>())
                        )
                        val expected = TemplateField(
                            id = existingEntity.id, // id는 변경되지 않음
                            name = existingEntity.name, // null이므로 변경되지 않음
                            icon = existingEntity.icon, // null이므로 변경되지 않음
                            type = existingEntity.type, // null이므로 변경되지 않음
                            order = existingEntity.order, // null이므로 변경되지 않음
                            isDisplay = dto.isDisplay!!, // 업데이트됨
                            attributes = existingEntity.attributes // 무시됨
                        )

                        it("기존 엔티티의 isDisplay 필드만 업데이트되어야 한다.") {
                            val result = templateFieldMapper.patchFromDto(dto, existingEntity)
                            result shouldBe expected
                        }
                    }
                    context("order와 icon만 들어있는 TemplateFieldPatchReqServiceDto가 주어지면,") {
                        val dto = TemplateFieldPatchReqServiceDto(
                            id = "dto-id", // 무시될 값
                            name = null,
                            icon = "new-icon",
                            type = null,
                            order = 5,
                            isDisplay = null,
                            attributes = setOf() // 무시될 값
                        )
                        val existingEntity = TemplateField(
                            id = "existing-id",
                            name = "Original Field",
                            icon = "original-icon",
                            type = "original-type",
                            order = 1,
                            isDisplay = true,
                            attributes = setOf(mockk<Attribute>())
                        )
                        val expected = TemplateField(
                            id = existingEntity.id, // id는 변경되지 않음
                            name = existingEntity.name, // null이므로 변경되지 않음
                            icon = dto.icon!!, // 업데이트됨
                            type = existingEntity.type, // null이므로 변경되지 않음
                            order = dto.order!!, // 업데이트됨
                            isDisplay = existingEntity.isDisplay, // null이므로 변경되지 않음
                            attributes = existingEntity.attributes // 무시됨
                        )

                        it("기존 엔티티의 order와 icon 필드만 업데이트되어야 한다.") {
                            val result = templateFieldMapper.patchFromDto(dto, existingEntity)
                            result shouldBe expected
                        }
                    }
                }
            }
        }
    }
}