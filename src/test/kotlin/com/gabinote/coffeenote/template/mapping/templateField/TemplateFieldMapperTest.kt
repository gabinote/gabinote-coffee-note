package com.gabinote.coffeenote.template.mapping.templateField

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeResControllerDto
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeUpdateReqControllerDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeResServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeUpdateReqServiceDto
import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapper
import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapperImpl
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
import java.util.*

@ContextConfiguration(classes = [TemplateFieldMapperImpl::class, AttributeMapperImpl::class])
class TemplateFieldMapperTest : MockkTestTemplate() {

    @Autowired
    lateinit var templateFieldMapper: TemplateFieldMapper

    @MockkBean
    lateinit var attributeMapper: AttributeMapper

    init {
        describe("[TemplateField] TemplateFieldMapper") {

            describe("TemplateFieldMapper.toResServiceDto") {
                context("TemplateField 엔티티가 주어지면,") {
                    val attribute = mockk<Attribute>()
                    val templateField = TemplateField(
                        id = UUID.randomUUID().toString(),
                        name = "Template Field Name",
                        icon = "icon.png",
                        type = "text",
                        attributes = setOf(attribute),
                        order = 1,
                        isDisplay = true
                    )

                    val expectedAttribute = mockk<AttributeResServiceDto>()

                    beforeTest {
                        every { attributeMapper.toAttributeResServiceDto(attribute) } returns expectedAttribute
                    }

                    val expected = TemplateFieldResServiceDto(
                        id = templateField.id,
                        name = templateField.name,
                        icon = templateField.icon,
                        type = templateField.type,
                        order = templateField.order,
                        isDisplay = templateField.isDisplay,
                        attributes = setOf(expectedAttribute)
                    )

                    it("TemplateFieldResServiceDto로 변환되어야 한다.") {
                        val result = templateFieldMapper.toResServiceDto(templateField)
                        result shouldBe expected

                        verify(exactly = 1) { attributeMapper.toAttributeResServiceDto(attribute) }
                    }
                }
            }

            describe("TemplateFieldMapper.toResControllerDto") {
                context("TemplateFieldResServiceDto가 주어지면,") {
                    val attributeDto = mockk<AttributeResServiceDto>()
                    val dto = TemplateFieldResServiceDto(
                        id = UUID.randomUUID().toString(),
                        name = "Template Field Name",
                        icon = "icon.png",
                        type = "text",
                        order = 1,
                        isDisplay = true,
                        attributes = setOf(attributeDto)
                    )

                    val expectedAttribute = mockk<AttributeResControllerDto>()

                    beforeTest {
                        every { attributeMapper.toAttributeResControllerDto(attributeDto) } returns expectedAttribute
                    }

                    val expected = TemplateFieldResControllerDto(
                        id = dto.id,
                        name = dto.name,
                        icon = dto.icon,
                        type = dto.type,
                        order = dto.order,
                        isDisplay = dto.isDisplay,
                        attributes = setOf(expectedAttribute)
                    )

                    it("TemplateFieldResControllerDto로 변환되어야 한다.") {
                        val result = templateFieldMapper.toResControllerDto(dto)
                        result shouldBe expected

                        verify(exactly = 1) { attributeMapper.toAttributeResControllerDto(attributeDto) }
                    }
                }
            }

            describe("TemplateFieldMapper.toCreateReqServiceDto") {
                context("올바른 TemplateFieldCreateReqControllerDto가 주어지면,") {
                    val attributeDto = mockk<AttributeCreateReqControllerDto>()
                    val dto = TemplateFieldCreateReqControllerDto(
                        id = UUID.randomUUID().toString(),
                        name = "Template Field Name",
                        icon = "icon.png",
                        type = "text",
                        order = 1,
                        isDisplay = true,
                        attributes = setOf(attributeDto)
                    )

                    val expectedAttribute = mockk<AttributeCreateReqServiceDto>()

                    beforeTest {
                        every { attributeMapper.toAttributeCreateReqServiceDto(attributeDto) } returns expectedAttribute
                    }

                    val expected = TemplateFieldCreateReqServiceDto(
                        id = dto.id,
                        name = dto.name,
                        icon = dto.icon,
                        type = dto.type,
                        order = dto.order,
                        isDisplay = dto.isDisplay,
                        attributes = setOf(expectedAttribute)
                    )

                    it("TemplateFieldCreateReqServiceDto로 변환되어야 한다.") {
                        val result = templateFieldMapper.toCreateReqServiceDto(dto)
                        result shouldBe expected

                        verify(exactly = 1) { attributeMapper.toAttributeCreateReqServiceDto(attributeDto) }
                    }
                }
            }

            describe("TemplateFieldMapper.toPatchReqServiceDto") {
                context("올바른 TemplateFieldPatchReqControllerDto가 주어지면,") {
                    val attributeDto = mockk<AttributeUpdateReqControllerDto>()
                    val dto = TemplateFieldPatchReqControllerDto(
                        id = UUID.randomUUID().toString(),
                        name = "Updated Template Field",
                        icon = "updated-icon.png",
                        type = "updated-type",
                        order = 2,
                        isDisplay = false,
                        attributes = setOf(attributeDto)
                    )

                    val expectedAttribute = mockk<AttributeUpdateReqServiceDto>()

                    beforeTest {
                        every { attributeMapper.toAttributeUpdateReqServiceDto(attributeDto) } returns expectedAttribute
                    }

                    val expected = TemplateFieldPatchReqServiceDto(
                        id = dto.id,
                        name = dto.name,
                        icon = dto.icon,
                        type = dto.type,
                        order = dto.order,
                        isDisplay = dto.isDisplay,
                        attributes = setOf(expectedAttribute)
                    )

                    it("TemplateFieldPatchReqServiceDto로 변환되어야 한다.") {
                        val result = templateFieldMapper.toPatchReqServiceDto(dto)
                        result shouldBe expected

                        verify(exactly = 1) { attributeMapper.toAttributeUpdateReqServiceDto(attributeDto) }
                    }
                }

                context("일부 필드만 설정된 TemplateFieldPatchReqControllerDto가 주어지면,") {
                    val dto = TemplateFieldPatchReqControllerDto(
                        id = UUID.randomUUID().toString(),
                        name = "Updated Template Field",
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
                    }
                }
            }

            describe("TemplateFieldMapper.toTemplateField") {
                context("올바른 TemplateFieldCreateReqServiceDto가 주어지면,") {
                    val attributeDto = mockk<AttributeCreateReqServiceDto>()
                    val dto = TemplateFieldCreateReqServiceDto(
                        id = UUID.randomUUID().toString(),
                        name = "Template Field Name",
                        icon = "icon.png",
                        type = "text",
                        order = 1,
                        isDisplay = true,
                        attributes = setOf(attributeDto)
                    )

                    val expectedAttribute = mockk<Attribute>()

                    beforeTest {
                        every { attributeMapper.toAttribute(attributeDto) } returns expectedAttribute
                    }

                    val expected = TemplateField(
                        id = dto.id,
                        name = dto.name,
                        icon = dto.icon,
                        type = dto.type,
                        order = dto.order,
                        isDisplay = dto.isDisplay,
                        attributes = setOf(expectedAttribute)
                    )

                    it("TemplateField 엔티티로 변환되어야 한다.") {
                        val result = templateFieldMapper.toTemplateField(dto)
                        result shouldBe expected

                        verify(exactly = 1) { attributeMapper.toAttribute(attributeDto) }
                    }
                }
            }

            describe("TemplateFieldMapper.patchFromDto") {
                context("모든 값이 들어있는 TemplateFieldPatchReqServiceDto와 기존 TemplateField 엔티티가 주어지면,") {
                    val dto = TemplateFieldPatchReqServiceDto(
                        id = UUID.randomUUID().toString(),
                        name = "Updated Template Field",
                        icon = "updated-icon.png",
                        type = "updated-type",
                        order = 2,
                        isDisplay = false,
                        attributes = setOf()
                    )

                    val existingEntity = TemplateField(
                        id = UUID.randomUUID().toString(),
                        name = "Template Field Name",
                        icon = "icon.png",
                        type = "text",
                        order = 1,
                        isDisplay = true,
                        attributes = setOf(mockk<Attribute>())
                    )

                    val expected = TemplateField(
                        id = existingEntity.id, // id는 무시됨
                        name = dto.name!!,
                        icon = dto.icon!!,
                        type = dto.type!!,
                        order = dto.order!!,
                        isDisplay = dto.isDisplay!!,
                        attributes = existingEntity.attributes // attributes는 무시됨
                    )

                    it("기존 엔티티의 필드들이 업데이트되어야 한다.") {
                        val result = templateFieldMapper.patchFromDto(dto, existingEntity)
                        result shouldBe expected
                    }
                }

                context("일부 값만 들어있는 TemplateFieldPatchReqServiceDto와 기존 TemplateField 엔티티가 주어지면,") {
                    val dto = TemplateFieldPatchReqServiceDto(
                        id = UUID.randomUUID().toString(), // 무시될 값
                        name = "Updated Template Field",
                        icon = null,
                        type = null,
                        order = null,
                        isDisplay = null,
                        attributes = setOf() // 무시될 값
                    )

                    val existingEntity = TemplateField(
                        id = UUID.randomUUID().toString(),
                        name = "Template Field Name",
                        icon = "icon.png",
                        type = "text",
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

                    it("null이 아닌 필드만 업데이트되어야 한다.") {
                        val result = templateFieldMapper.patchFromDto(dto, existingEntity)
                        result shouldBe expected
                    }
                }
            }
        }
    }
}