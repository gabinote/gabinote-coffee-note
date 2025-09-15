package com.gabinote.coffeenote.template.mapping.template

import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapperImpl
import com.gabinote.coffeenote.template.domain.template.Template
import com.gabinote.coffeenote.template.domain.templateField.TemplateField
import com.gabinote.coffeenote.template.dto.template.controller.*
import com.gabinote.coffeenote.template.dto.template.service.*
import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldCreateReqControllerDto
import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldResControllerDto
import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldCreateReqServiceDto
import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldResServiceDto
import com.gabinote.coffeenote.template.mapping.templateField.TemplateFieldMapper
import com.gabinote.coffeenote.template.mapping.templateField.TemplateFieldMapperImpl
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import java.util.*

@ContextConfiguration(classes = [TemplateMapperImpl::class, TemplateFieldMapperImpl::class, AttributeMapperImpl::class])
class TemplateMapperTest : MockkTestTemplate() {

    @Autowired
    lateinit var templateMapper: TemplateMapper

    @MockkBean
    lateinit var templateFieldMapper: TemplateFieldMapper

    init {
        describe("[Template] TemplateMapper Test") {

            describe("TemplateMapper.toCreateReqServiceDto") {
                context("올바른 TemplateCreateReqControllerDto와 owner가 주어지면,") {
                    val fieldControllerDto = mockk<TemplateFieldCreateReqControllerDto>()
                    val dto = TemplateCreateReqControllerDto(
                        name = "Template Name",
                        icon = "template-icon",
                        description = "Template Description",
                        isOpen = true,
                        fields = listOf(fieldControllerDto)
                    )
                    val owner = "owner-id"
                    val fieldServiceDto = mockk<TemplateFieldCreateReqServiceDto>()
                    val expected = TemplateCreateReqServiceDto(
                        name = dto.name,
                        icon = dto.icon,
                        description = dto.description,
                        isOpen = dto.isOpen,
                        owner = owner,
                        fields = listOf(fieldServiceDto)
                    )

                    beforeTest {
                        every { templateFieldMapper.toCreateReqServiceDto(fieldControllerDto) } returns fieldServiceDto
                    }

                    it("TemplateCreateReqServiceDto로 변환되어야 한다.") {
                        val result = templateMapper.toCreateReqServiceDto(dto, owner)
                        result shouldBe expected

                        verify(exactly = 1) { templateFieldMapper.toCreateReqServiceDto(fieldControllerDto) }
                    }
                }
                context("fields가 빈 TemplateCreateReqControllerDto와 owner가 주어지면,") {
                    val dto = TemplateCreateReqControllerDto(
                        name = "Empty Template",
                        icon = "empty-icon",
                        description = "Empty Description",
                        isOpen = false,
                        fields = emptyList()
                    )
                    val owner = "owner-id"
                    val expected = TemplateCreateReqServiceDto(
                        name = dto.name,
                        icon = dto.icon,
                        description = dto.description,
                        isOpen = dto.isOpen,
                        owner = owner,
                        fields = emptyList()
                    )

                    it("빈 fields를 가진 TemplateCreateReqServiceDto로 변환되어야 한다.") {
                        val result = templateMapper.toCreateReqServiceDto(dto, owner)
                        result shouldBe expected

                        verify(exactly = 0) { templateFieldMapper.toCreateReqServiceDto(any()) }
                    }
                }
            }

            describe("TemplateMapper.toTemplate") {
                context("올바른 TemplateCreateReqServiceDto가 주어지면,") {
                    val fieldServiceDto = mockk<TemplateFieldCreateReqServiceDto>()
                    val dto = TemplateCreateReqServiceDto(
                        name = "Template Name",
                        icon = "template-icon",
                        description = "Template Description",
                        isOpen = true,
                        owner = "owner-id",
                        fields = listOf(fieldServiceDto)
                    )
                    val templateField = mockk<TemplateField>()
                    val expected = Template(
                        id = null, // @Mapping(target = "id", ignore = true)
                        externalId = null, // @Mapping(target = "externalId", ignore = true)
                        name = dto.name,
                        icon = dto.icon,
                        description = dto.description,
                        isOpen = dto.isOpen,
                        owner = dto.owner,
                        isDefault = false, // @Mapping(target = "isDefault", constant = "false")
                        fields = listOf(templateField)
                    )

                    beforeTest {
                        every { templateFieldMapper.toTemplateField(fieldServiceDto) } returns templateField
                    }

                    it("Template 엔티티로 변환되어야 한다.") {
                        val result = templateMapper.toTemplate(dto)
                        result shouldBe expected

                        verify(exactly = 1) { templateFieldMapper.toTemplateField(fieldServiceDto) }
                    }
                }
                context("fields가 빈 TemplateCreateReqServiceDto가 주어지면,") {
                    val dto = TemplateCreateReqServiceDto(
                        name = "Empty Template",
                        icon = "empty-icon",
                        description = "Empty Description",
                        isOpen = false,
                        owner = "owner-id",
                        fields = emptyList()
                    )
                    val expected = Template(
                        id = null, // @Mapping(target = "id", ignore = true)
                        externalId = null, // @Mapping(target = "externalId", ignore = true)
                        name = dto.name,
                        icon = dto.icon,
                        description = dto.description,
                        isOpen = dto.isOpen,
                        owner = dto.owner,
                        isDefault = false, // @Mapping(target = "isDefault", constant = "false")
                        fields = emptyList()
                    )

                    it("빈 fields를 가진 Template 엔티티로 변환되어야 한다.") {
                        val result = templateMapper.toTemplate(dto)
                        result shouldBe expected

                        verify(exactly = 0) { templateFieldMapper.toTemplateField(any()) }
                    }
                }
            }

            describe("TemplateMapper.toCreateDefaultReqServiceDto") {
                context("올바른 TemplateCreateDefaultReqControllerDto가 주어지면,") {
                    val fieldControllerDto = mockk<TemplateFieldCreateReqControllerDto>()
                    val dto = TemplateCreateDefaultReqControllerDto(
                        name = "Default Template",
                        icon = "default-icon",
                        description = "Default Description",
                        fields = listOf(fieldControllerDto)
                    )
                    val fieldServiceDto = mockk<TemplateFieldCreateReqServiceDto>()
                    val expected = TemplateCreateDefaultReqServiceDto(
                        name = dto.name,
                        icon = dto.icon,
                        description = dto.description,
                        fields = listOf(fieldServiceDto)
                    )

                    beforeTest {
                        every { templateFieldMapper.toCreateReqServiceDto(fieldControllerDto) } returns fieldServiceDto
                    }

                    it("TemplateCreateDefaultReqServiceDto로 변환되어야 한다.") {
                        val result = templateMapper.toCreateDefaultReqServiceDto(dto)
                        result shouldBe expected

                        verify(exactly = 1) { templateFieldMapper.toCreateReqServiceDto(fieldControllerDto) }
                    }
                }
                context("fields가 빈 TemplateCreateDefaultReqControllerDto가 주어지면,") {
                    val dto = TemplateCreateDefaultReqControllerDto(
                        name = "Empty Default Template",
                        icon = "empty-default-icon",
                        description = "Empty Default Description",
                        fields = emptyList()
                    )
                    val expected = TemplateCreateDefaultReqServiceDto(
                        name = dto.name,
                        icon = dto.icon,
                        description = dto.description,
                        fields = emptyList()
                    )

                    it("빈 fields를 가진 TemplateCreateDefaultReqServiceDto로 변환되어야 한다.") {
                        val result = templateMapper.toCreateDefaultReqServiceDto(dto)
                        result shouldBe expected

                        verify(exactly = 0) { templateFieldMapper.toCreateReqServiceDto(any()) }
                    }
                }
            }

            describe("TemplateMapper.toDefaultTemplate") {
                context("올바른 TemplateCreateDefaultReqServiceDto가 주어지면,") {
                    val fieldServiceDto = mockk<TemplateFieldCreateReqServiceDto>()
                    val dto = TemplateCreateDefaultReqServiceDto(
                        name = "Default Template",
                        icon = "default-icon",
                        description = "Default Description",
                        fields = listOf(fieldServiceDto)
                    )
                    val templateField = mockk<TemplateField>()
                    val expected = Template(
                        id = null, // @Mapping(target = "id", ignore = true)
                        externalId = null, // @Mapping(target = "externalId", ignore = true)
                        name = dto.name,
                        icon = dto.icon,
                        description = dto.description,
                        isOpen = true, // @Mapping(target = "isOpen", constant = "true")
                        owner = null, // @Mapping(target = "owner", ignore = true)
                        isDefault = true, // @Mapping(target = "isDefault", constant = "true")
                        fields = listOf(templateField)
                    )

                    beforeTest {
                        every { templateFieldMapper.toTemplateField(fieldServiceDto) } returns templateField
                    }

                    it("기본 템플릿 설정이 적용된 Template 엔티티로 변환되어야 한다.") {
                        val result = templateMapper.toDefaultTemplate(dto)
                        result shouldBe expected

                        verify(exactly = 1) { templateFieldMapper.toTemplateField(fieldServiceDto) }
                    }
                }
                context("fields가 빈 TemplateCreateDefaultReqServiceDto가 주어지면,") {
                    val dto = TemplateCreateDefaultReqServiceDto(
                        name = "Empty Default Template",
                        icon = "empty-default-icon",
                        description = "Empty Default Description",
                        fields = emptyList()
                    )
                    val expected = Template(
                        id = null, // @Mapping(target = "id", ignore = true)
                        externalId = null, // @Mapping(target = "externalId", ignore = true)
                        name = dto.name,
                        icon = dto.icon,
                        description = dto.description,
                        isOpen = true, // @Mapping(target = "isOpen", constant = "true")
                        owner = null, // @Mapping(target = "owner", ignore = true)
                        isDefault = true, // @Mapping(target = "isDefault", constant = "true")
                        fields = emptyList()
                    )

                    it("빈 fields를 가진 기본 템플릿 Template 엔티티로 변환되어야 한다.") {
                        val result = templateMapper.toDefaultTemplate(dto)
                        result shouldBe expected

                        verify(exactly = 0) { templateFieldMapper.toTemplateField(any()) }
                    }
                }
            }

            describe("TemplateMapper.toUpdateDefaultReqServiceDto") {
                context("올바른 TemplateUpdateDefaultReqControllerDto와 externalId가 주어지면,") {
                    val fieldServiceDto = mockk<TemplateFieldCreateReqServiceDto>()
                    val dto = TemplateUpdateDefaultReqControllerDto(
                        name = "Updated Default Template",
                        icon = "updated-default-icon",
                        description = "Updated Default Description",
                        fields = listOf(fieldServiceDto)
                    )
                    val externalId = UUID.randomUUID()
                    val expected = TemplateUpdateDefaultReqServiceDto(
                        externalId = externalId,
                        name = dto.name,
                        icon = dto.icon,
                        description = dto.description,
                        fields = dto.fields
                    )

                    it("TemplateUpdateDefaultReqServiceDto로 변환되어야 한다.") {
                        val result = templateMapper.toUpdateDefaultReqServiceDto(dto, externalId)
                        result shouldBe expected
                    }
                }
                context("fields가 빈 TemplateUpdateDefaultReqControllerDto가 주어지면,") {
                    val dto = TemplateUpdateDefaultReqControllerDto(
                        name = "Empty Updated Default Template",
                        icon = "empty-updated-icon",
                        description = "Empty Updated Description",
                        fields = emptyList()
                    )
                    val externalId = UUID.randomUUID()
                    val expected = TemplateUpdateDefaultReqServiceDto(
                        externalId = externalId,
                        name = dto.name,
                        icon = dto.icon,
                        description = dto.description,
                        fields = emptyList()
                    )

                    it("빈 fields를 가진 TemplateUpdateDefaultReqServiceDto로 변환되어야 한다.") {
                        val result = templateMapper.toUpdateDefaultReqServiceDto(dto, externalId)
                        result shouldBe expected
                    }
                }
            }

            describe("TemplateMapper.toUpdateReqServiceDto") {
                context("올바른 TemplateUpdateReqControllerDto, owner, externalId가 주어지면,") {
                    val fieldServiceDto = mockk<TemplateFieldCreateReqServiceDto>()
                    val dto = TemplateUpdateReqControllerDto(
                        name = "Updated Template",
                        icon = "updated-icon",
                        description = "Updated Description",
                        isOpen = true,
                        fields = listOf(fieldServiceDto)
                    )
                    val owner = "owner-id"
                    val externalId = UUID.randomUUID()
                    val expected = TemplateUpdateReqServiceDto(
                        externalId = externalId,
                        name = dto.name,
                        icon = dto.icon,
                        description = dto.description,
                        isOpen = dto.isOpen,
                        owner = owner,
                        fields = dto.fields
                    )

                    it("TemplateUpdateReqServiceDto로 변환되어야 한다.") {
                        val result = templateMapper.toUpdateReqServiceDto(dto, owner, externalId)
                        result shouldBe expected
                    }
                }
                context("fields가 빈 TemplateUpdateReqControllerDto가 주어지면,") {
                    val dto = TemplateUpdateReqControllerDto(
                        name = "Empty Updated Template",
                        icon = "empty-updated-icon",
                        description = "Empty Updated Description",
                        isOpen = true,
                        fields = emptyList()
                    )
                    val owner = "owner-id"
                    val externalId = UUID.randomUUID()
                    val expected = TemplateUpdateReqServiceDto(
                        externalId = externalId,
                        name = dto.name,
                        icon = dto.icon,
                        description = dto.description,
                        isOpen = dto.isOpen,
                        owner = owner,
                        fields = emptyList()
                    )

                    it("빈 fields를 가진 TemplateUpdateReqServiceDto로 변환되어야 한다.") {
                        val result = templateMapper.toUpdateReqServiceDto(dto, owner, externalId)
                        result shouldBe expected
                    }
                }
            }

            describe("TemplateMapper.toResServiceDto") {
                context("올바른 Template 엔티티가 주어지면,") {
                    val templateField = mockk<TemplateField>()
                    val template = Template(
                        id = ObjectId("507f1f77bcf86cd799439011"),
                        externalId = "template-external-id",
                        name = "Template Name",
                        icon = "template-icon",
                        description = "Template Description",
                        isOpen = true,
                        owner = "owner-id",
                        isDefault = false,
                        fields = listOf(templateField)
                    )
                    val fieldResServiceDto = mockk<TemplateFieldResServiceDto>()
                    val expected = TemplateResServiceDto(
                        id = template.id!!,
                        externalId = template.externalId!!,
                        name = template.name,
                        icon = template.icon,
                        description = template.description,
                        isOpen = template.isOpen,
                        owner = template.owner,
                        isDefault = template.isDefault,
                        fields = listOf(fieldResServiceDto)
                    )

                    beforeTest {
                        every { templateFieldMapper.toResServiceDto(templateField) } returns fieldResServiceDto
                    }

                    it("TemplateResServiceDto로 변환되어야 한다.") {
                        val result = templateMapper.toResServiceDto(template)
                        result shouldBe expected

                        verify(exactly = 1) { templateFieldMapper.toResServiceDto(templateField) }
                    }
                }
                context("기본 템플릿인 Template 엔티티가 주어지면,") {
                    val templateField = mockk<TemplateField>()
                    val template = Template(
                        id = ObjectId("507f1f77bcf86cd799439012"),
                        externalId = "default-template-external-id",
                        name = "Default Template",
                        icon = "default-icon",
                        description = "Default Template Description",
                        isOpen = true,
                        owner = null, // 기본 템플릿은 owner가 null
                        isDefault = true,
                        fields = listOf(templateField)
                    )
                    val fieldResServiceDto = mockk<TemplateFieldResServiceDto>()
                    val expected = TemplateResServiceDto(
                        id = template.id!!,
                        externalId = template.externalId!!,
                        name = template.name,
                        icon = template.icon,
                        description = template.description,
                        isOpen = template.isOpen,
                        owner = null,
                        isDefault = true,
                        fields = listOf(fieldResServiceDto)
                    )

                    beforeTest {
                        every { templateFieldMapper.toResServiceDto(templateField) } returns fieldResServiceDto
                    }

                    it("기본 템플릿 설정이 적용된 TemplateResServiceDto로 변환되어야 한다.") {
                        val result = templateMapper.toResServiceDto(template)
                        result shouldBe expected

                        verify(exactly = 1) { templateFieldMapper.toResServiceDto(templateField) }
                    }
                }
                context("fields가 빈 Template 엔티티가 주어지면,") {
                    val template = Template(
                        id = ObjectId("507f1f77bcf86cd799439013"),
                        externalId = "empty-template-external-id",
                        name = "Empty Template",
                        icon = "empty-icon",
                        description = "Empty Template Description",
                        isOpen = false,
                        owner = "owner-id",
                        isDefault = false,
                        fields = emptyList()
                    )
                    val expected = TemplateResServiceDto(
                        id = template.id!!,
                        externalId = template.externalId!!,
                        name = template.name,
                        icon = template.icon,
                        description = template.description,
                        isOpen = template.isOpen,
                        owner = template.owner,
                        isDefault = template.isDefault,
                        fields = emptyList()
                    )

                    it("빈 fields를 가진 TemplateResServiceDto로 변환되어야 한다.") {
                        val result = templateMapper.toResServiceDto(template)
                        result shouldBe expected

                        verify(exactly = 0) { templateFieldMapper.toResServiceDto(any()) }
                    }
                }
            }

            describe("TemplateMapper.toResControllerDto") {
                context("올바른 TemplateResServiceDto가 주어지면,") {
                    val fieldResServiceDto = mockk<TemplateFieldResServiceDto>()
                    val dto = TemplateResServiceDto(
                        id = ObjectId("507f1f77bcf86cd799439011"),
                        externalId = "template-external-id",
                        name = "Template Name",
                        icon = "template-icon",
                        description = "Template Description",
                        isOpen = true,
                        owner = "owner-id",
                        isDefault = false,
                        fields = listOf(fieldResServiceDto)
                    )
                    val fieldResControllerDto = mockk<TemplateFieldResControllerDto>()
                    val expected = TemplateResControllerDto(
                        externalId = dto.externalId,
                        name = dto.name,
                        icon = dto.icon,
                        description = dto.description,
                        isOpen = dto.isOpen,
                        owner = dto.owner,
                        isDefault = dto.isDefault,
                        fields = listOf(fieldResControllerDto)
                    )

                    beforeTest {
                        every { templateFieldMapper.toResControllerDto(fieldResServiceDto) } returns fieldResControllerDto
                    }

                    it("TemplateResControllerDto로 변환되어야 한다.") {
                        val result = templateMapper.toResControllerDto(dto)
                        result shouldBe expected

                        verify(exactly = 1) { templateFieldMapper.toResControllerDto(fieldResServiceDto) }
                    }
                }
                context("기본 템플릿인 TemplateResServiceDto가 주어지면,") {
                    val fieldResServiceDto = mockk<TemplateFieldResServiceDto>()
                    val dto = TemplateResServiceDto(
                        id = ObjectId("507f1f77bcf86cd799439012"),
                        externalId = "default-template-external-id",
                        name = "Default Template",
                        icon = "default-icon",
                        description = "Default Template Description",
                        isOpen = true,
                        owner = null, // 기본 템플릿은 owner가 null
                        isDefault = true,
                        fields = listOf(fieldResServiceDto)
                    )
                    val fieldResControllerDto = mockk<TemplateFieldResControllerDto>()
                    val expected = TemplateResControllerDto(
                        externalId = dto.externalId,
                        name = dto.name,
                        icon = dto.icon,
                        description = dto.description,
                        isOpen = dto.isOpen,
                        owner = null,
                        isDefault = true,
                        fields = listOf(fieldResControllerDto)
                    )

                    beforeTest {
                        every { templateFieldMapper.toResControllerDto(fieldResServiceDto) } returns fieldResControllerDto
                    }

                    it("기본 템플릿 설정이 적용된 TemplateResControllerDto로 변환되어야 한다.") {
                        val result = templateMapper.toResControllerDto(dto)
                        result shouldBe expected

                        verify(exactly = 1) { templateFieldMapper.toResControllerDto(fieldResServiceDto) }
                    }
                }
                context("fields가 빈 TemplateResServiceDto가 주어지면,") {
                    val dto = TemplateResServiceDto(
                        id = ObjectId("507f1f77bcf86cd799439013"),
                        externalId = "empty-template-external-id",
                        name = "Empty Template",
                        icon = "empty-icon",
                        description = "Empty Template Description",
                        isOpen = false,
                        owner = "owner-id",
                        isDefault = false,
                        fields = emptyList()
                    )
                    val expected = TemplateResControllerDto(
                        externalId = dto.externalId,
                        name = dto.name,
                        icon = dto.icon,
                        description = dto.description,
                        isOpen = dto.isOpen,
                        owner = dto.owner,
                        isDefault = dto.isDefault,
                        fields = emptyList()
                    )

                    it("빈 fields를 가진 TemplateResControllerDto로 변환되어야 한다.") {
                        val result = templateMapper.toResControllerDto(dto)
                        result shouldBe expected

                        verify(exactly = 0) { templateFieldMapper.toResControllerDto(any()) }
                    }
                }
            }

            describe("TemplateMapper.updateFromDto") {
                context("올바른 TemplateUpdateReqServiceDto와 기존 Template 엔티티가 주어지면,") {
                    val dto = TemplateUpdateReqServiceDto(
                        externalId = UUID.randomUUID(), // 무시될 값
                        name = "Updated Template Name",
                        icon = "updated-icon",
                        description = "Updated Description",
                        isOpen = false,
                        owner = "dto-owner", // 무시될 값
                        fields = listOf(mockk()) // 무시될 값
                    )
                    val existingEntity = Template(
                        id = ObjectId("507f1f77bcf86cd799439011"),
                        externalId = "existing-external-id",
                        name = "Original Template",
                        icon = "original-icon",
                        description = "Original Description",
                        isOpen = true,
                        owner = "original-owner",
                        isDefault = false,
                        fields = listOf(mockk<TemplateField>())
                    )
                    val expected = Template(
                        id = existingEntity.id, // @Mapping(target = "id", ignore = true)
                        externalId = existingEntity.externalId, // @Mapping(target = "externalId", ignore = true)
                        name = dto.name, // 업데이트됨
                        icon = dto.icon, // 업데이트됨
                        description = dto.description, // 업데이트됨
                        isOpen = dto.isOpen, // 업데이트됨
                        owner = existingEntity.owner, // @Mapping(target = "owner", ignore = true)
                        isDefault = existingEntity.isDefault, // @Mapping(target = "isDefault", ignore = true)
                        fields = existingEntity.fields // @Mapping(target = "fields", ignore = true)
                    )

                    it("기존 엔티티의 지정된 필드들이 업데이트되어야 한다.") {
                        val result = templateMapper.updateFromDto(dto, existingEntity)
                        result shouldBe expected
                    }
                }
            }

            describe("TemplateMapper.updateDefaultFromDto") {
                context("TemplateUpdateDefaultReqServiceDto와 기존 Template 엔티티가 주어지면,") {
                    val dto = TemplateUpdateDefaultReqServiceDto(
                        externalId = UUID.randomUUID(), // 무시될 값
                        name = "Updated Default Template",
                        icon = "updated-default-icon",
                        description = "Updated Default Description",
                        fields = listOf(mockk()) // 무시될 값
                    )
                    val existingEntity = Template(
                        id = ObjectId("507f1f77bcf86cd799439014"),
                        externalId = "existing-default-external-id",
                        name = "Original Default Template",
                        icon = "original-default-icon",
                        description = "Original Default Description",
                        isOpen = true,
                        owner = null, // 기본 템플릿은 owner가 null
                        isDefault = true,
                        fields = listOf(mockk<TemplateField>())
                    )
                    val expected = Template(
                        id = existingEntity.id, // @Mapping(target = "id", ignore = true)
                        externalId = existingEntity.externalId, // @Mapping(target = "externalId", ignore = true)
                        name = dto.name, // 업데이트됨
                        icon = dto.icon, // 업데이트됨
                        description = dto.description, // 업데이트됨
                        isOpen = existingEntity.isOpen, // 기존 값 유지
                        owner = null, // @Mapping(target = "owner", ignore = true)
                        isDefault = true, // @Mapping(target = "isDefault", ignore = true)
                        fields = existingEntity.fields // @Mapping(target = "fields", ignore = true)
                    )

                    it("기존 기본 템플릿 엔티티의 지정된 필드들이 업데이트되어야 한다.") {
                        val result = templateMapper.updateDefaultFromDto(dto, existingEntity)
                        result shouldBe expected
                    }
                }
            }
        }
    }
}
