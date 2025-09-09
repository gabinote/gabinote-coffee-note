package com.gabinote.coffeenote.field.mapping.field

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.field.Field
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeResControllerDto
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeUpdateReqControllerDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeResServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeUpdateReqServiceDto
import com.gabinote.coffeenote.field.dto.field.controller.*
import com.gabinote.coffeenote.field.dto.field.service.*
import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapper
import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapperImpl
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

@ContextConfiguration(classes = [FieldMapperImpl::class, AttributeMapperImpl::class])
class FieldMapperTest : MockkTestTemplate() {

    @Autowired
    lateinit var fieldMapper: FieldMapper

    @MockkBean
    lateinit var attributeMapper: AttributeMapper

    init {
        describe("[Field] FieldMapper") {

            describe("FieldMapper.toResServiceDto") {
                context("isDefault가 false인 Field 엔티티가 주어지면,") {
                    val field = Field(
                        id = mockk<ObjectId>(),
                        externalId = "field-external-id",
                        owner = "owner-id",
                        name = "Field Name",
                        icon = "field-icon",
                        type = "custom",
                        isDefault = false,
                        attributes = setOf(mockk<Attribute>())
                    )
                    val expected = FieldResServiceDto(
                        id = field.id!!,
                        externalId = field.externalId!!,
                        name = field.name,
                        icon = field.icon,
                        type = field.type,
                        attributes = setOf(mockk<AttributeResServiceDto>()),
                        owner = field.owner,
                        isDefault = field.isDefault,
                    )

                    beforeEach {
                        every { attributeMapper.toAttributeResServiceDto(field.attributes.first()) } returns expected.attributes.first()
                    }

                    it("FieldResServiceDto로 변환되어야 한다.") {
                        val result = fieldMapper.toResServiceDto(field)
                        result shouldBe expected

                        verify(exactly = 1) { attributeMapper.toAttributeResServiceDto(field.attributes.first()) }
                    }
                }

                context("isDefault가 true인 Field 엔티티가 주어지면,") {
                    val field = Field(
                        id = mockk<ObjectId>(),
                        externalId = "field-external-id",
                        owner = "owner-id",
                        name = "Field Name",
                        icon = "field-icon",
                        type = "custom",
                        isDefault = true,
                        attributes = setOf(mockk<Attribute>())
                    )
                    val expected = FieldResServiceDto(
                        id = field.id!!,
                        externalId = field.externalId!!,
                        name = field.name,
                        icon = field.icon,
                        type = field.type,
                        attributes = setOf(mockk<AttributeResServiceDto>()),
                        owner = field.owner,
                        isDefault = field.isDefault,
                    )

                    beforeEach {
                        every { attributeMapper.toAttributeResServiceDto(field.attributes.first()) } returns expected.attributes.first()
                    }

                    it("FieldResServiceDto로 변환되어야 한다.") {
                        val result = fieldMapper.toResServiceDto(field)
                        result shouldBe expected

                        verify(exactly = 1) { attributeMapper.toAttributeResServiceDto(field.attributes.first()) }
                    }
                }
            }



            describe("FieldMapper.toResControllerDto") {
                context("isDefault가 false인 FieldResServiceDto가 주어지면,") {
                    val dto = FieldResServiceDto(
                        id = mockk<ObjectId>(),
                        externalId = "field-external-id",
                        owner = "owner-id",
                        name = "Field Name",
                        icon = "field-icon",
                        type = "custom",
                        attributes = setOf(mockk<AttributeResServiceDto>()),
                        isDefault = false,
                    )
                    val expected = FieldResControllerDto(
                        externalId = dto.externalId,
                        owner = dto.owner,
                        name = dto.name,
                        icon = dto.icon,
                        type = dto.type,
                        attributes = setOf(mockk<AttributeResControllerDto>()),
                        isDefault = dto.isDefault,
                    )

                    beforeEach {
                        every { attributeMapper.toAttributeResControllerDto(dto.attributes.first()) } returns expected.attributes.first()
                    }

                    it("FieldResControllerDto로 변환되어야 한다.") {
                        val result = fieldMapper.toResControllerDto(dto)
                        result shouldBe expected

                        verify(exactly = 1) { attributeMapper.toAttributeResControllerDto(dto.attributes.first()) }
                    }
                }
                context("isDefault가 true인 FieldResServiceDto가 주어지면,") {
                    val dto = FieldResServiceDto(
                        id = mockk<ObjectId>(),
                        externalId = "field-external-id",
                        owner = "owner-id",
                        name = "Field Name",
                        icon = "field-icon",
                        type = "custom",
                        attributes = setOf(mockk<AttributeResServiceDto>()),
                        isDefault = true,
                    )
                    val expected = FieldResControllerDto(
                        externalId = dto.externalId,
                        owner = dto.owner,
                        name = dto.name,
                        icon = dto.icon,
                        type = dto.type,
                        attributes = setOf(mockk<AttributeResControllerDto>()),
                        isDefault = dto.isDefault,
                    )

                    beforeEach {
                        every { attributeMapper.toAttributeResControllerDto(dto.attributes.first()) } returns expected.attributes.first()
                    }

                    it("FieldResControllerDto로 변환되어야 한다.") {
                        val result = fieldMapper.toResControllerDto(dto)
                        result shouldBe expected

                        verify(exactly = 1) { attributeMapper.toAttributeResControllerDto(dto.attributes.first()) }
                    }
                }
            }

            describe("FieldMapper.toCreateReqServiceDto") {
                context("올바른 FieldCreateReqControllerDto 가 주어지면,") {
                    var dto = FieldCreateReqControllerDto(
                        name = "Field Name",
                        icon = "field-icon",
                        type = "custom",
                        attributes = setOf(mockk<AttributeCreateReqControllerDto>()),
                    )

                    var expectedOwner = "owner-id"

                    var expected = FieldCreateReqServiceDto(
                        name = dto.name,
                        icon = dto.icon,
                        type = dto.type,
                        attributes = setOf(mockk<AttributeCreateReqServiceDto>()),
                        owner = expectedOwner,
                    )

                    beforeEach {
                        every { attributeMapper.toAttributeCreateReqServiceDto(dto.attributes.first()) } returns expected.attributes.first()
                    }

                    it("FieldCreateReqServiceDto 로 변환되어야 한다.") {
                        val result = fieldMapper.toCreateReqServiceDto(dto, expectedOwner)
                        result shouldBe expected

                        verify(exactly = 1) { attributeMapper.toAttributeCreateReqServiceDto(dto.attributes.first()) }
                    }
                }
            }

            describe("FieldMapper.toField") {
                context("올바른 FieldCreateReqServiceDto 가 주어지면,") {
                    var dto = FieldCreateReqServiceDto(
                        name = "Field Name",
                        icon = "field-icon",
                        type = "custom",
                        attributes = setOf(mockk<AttributeCreateReqServiceDto>()),
                        owner = "owner-id",
                    )

                    var expected = Field(
                        id = null,
                        externalId = null,
                        name = dto.name,
                        icon = dto.icon,
                        type = dto.type,
                        attributes = setOf(mockk<Attribute>()),
                        owner = dto.owner,
                        isDefault = false,
                    )

                    beforeEach {
                        every { attributeMapper.toAttribute(dto.attributes.first()) } returns expected.attributes.first()
                    }

                    it("Field 엔티티로 변환되어야 한다.") {
                        val result = fieldMapper.toField(dto)
                        result shouldBe expected

                        verify(exactly = 1) { attributeMapper.toAttribute(dto.attributes.first()) }
                    }
                }
            }

            describe("FieldMapper.toUpdateReqServiceDto") {
                context("올바른 FieldUpdateReqControllerDto, ownerId, externalId 가 주어지면,") {
                    val dto = FieldUpdateReqControllerDto(
                        name = "Field Name",
                        icon = "field-icon",
                        attributes = setOf(mockk<AttributeUpdateReqControllerDto>()),
                    )

                    val expectedOwnerId = "owner-id"
                    val expectedExternalId = UUID.randomUUID()

                    val expected = FieldUpdateReqServiceDto(
                        name = dto.name,
                        icon = dto.icon,
                        attributes = setOf(mockk<AttributeUpdateReqServiceDto>()),
                        owner = expectedOwnerId,
                        externalId = expectedExternalId,
                    )

                    beforeEach {
                        every { attributeMapper.toAttributeUpdateReqServiceDto(dto.attributes.first()) } returns expected.attributes.first()
                    }

                    it("FieldUpdateReqServiceDto 로 변환되어야 한다.") {
                        val result = fieldMapper.toUpdateReqServiceDto(
                            dto = dto,
                            owner = expectedOwnerId,
                            externalId = expectedExternalId
                        )
                        result shouldBe expected

                        verify(exactly = 1) { attributeMapper.toAttributeUpdateReqServiceDto(dto.attributes.first()) }
                    }
                }
            }

            describe("FieldMapper.updateFromDto") {
                context("모든 값이 들어있는 FieldUpdateReqServiceDto와 기존 Field 엔티티가 주어지면,") {
                    val dto = FieldUpdateReqServiceDto(
                        name = "Updated Field",
                        icon = "updated-icon",
                        attributes = setOf(),
                        externalId = UUID.randomUUID(),
                        owner = "owner-id"
                    )

                    val existingField = Field(
                        id = null,
                        externalId = dto.externalId.toString(),
                        name = "Old Field",
                        icon = "old-icon",
                        type = "TEXT",
                        attributes = setOf(),
                        owner = "owner-id"
                    )

                    val expected = Field(
                        id = existingField.id,
                        externalId = existingField.externalId,
                        name = dto.name!!,
                        icon = dto.icon!!,
                        type = existingField.type,
                        attributes = existingField.attributes,
                        owner = existingField.owner
                    )

                    it("기존 Field 엔티티의 name, icon 필드가 업데이트되어야 한다.") {
                        val result = fieldMapper.updateFromDto(dto, existingField)
                        result shouldBe expected
                    }
                }

                context("name만 들어있는 FieldUpdateReqServiceDto가 주어지면,") {
                    val dto = FieldUpdateReqServiceDto(
                        name = "Updated Field",
                        icon = null,
                        externalId = UUID.randomUUID(),
                        owner = "owner-id"
                    )

                    val existingField = Field(
                        name = "Old Field",
                        icon = "old-icon",
                        type = "TEXT",
                        attributes = setOf(),
                        owner = "owner-id"
                    )

                    val expected = Field(
                        name = dto.name!!,
                        icon = existingField.icon, // icon is not updated
                        type = existingField.type, // type is not updated
                        attributes = existingField.attributes, // attributes are not updated
                        owner = existingField.owner
                    )

                    it("기존 Field 엔티티의 name 필드만 업데이트되어야 한다.") {
                        val result = fieldMapper.updateFromDto(dto, existingField)
                        result shouldBe expected
                    }
                }

                context("icon만 들어있는 FieldUpdateReqServiceDto가 주어지면,") {
                    val dto = FieldUpdateReqServiceDto(
                        name = null,
                        icon = "new-icon",
                        externalId = UUID.randomUUID(),
                        owner = "owner-id"
                    )

                    val existingField = Field(
                        name = "Old Field",
                        icon = "old-icon",
                        type = "TEXT",
                        attributes = setOf(),
                        owner = "owner-id"
                    )

                    val expected = Field(
                        name = existingField.name, // name is not updated
                        icon = dto.icon!!, // icon is updated
                        type = existingField.type, // type is not updated
                        attributes = existingField.attributes, // attributes are not updated
                        owner = existingField.owner
                    )

                    it("기존 Field 엔티티의 icon 필드만 업데이트되어야 한다.") {
                        val result = fieldMapper.updateFromDto(dto, existingField)
                        result shouldBe expected
                    }
                }
            }

            describe("FieldMapper.toCreateDefaultReqServiceDto") {
                context("올바른 FieldCreateDefaultReqControllerDto 가 주어지면,") {

                    val dto = FieldCreateDefaultReqControllerDto(
                        name = "기본 필드",
                        icon = "default-icon",
                        type = "TEXT",
                        attributes = setOf(mockk<AttributeCreateReqControllerDto>()),
                    )

                    val expected = FieldCreateDefaultReqServiceDto(
                        name = "기본 필드",
                        icon = "default-icon",
                        type = "TEXT",
                        attributes = setOf(mockk<AttributeCreateReqServiceDto>()),
                    )

                    beforeEach {
                        every { attributeMapper.toAttributeCreateReqServiceDto(dto.attributes.first()) } returns expected.attributes.first()
                    }


                    it("FieldCreateDefaultReqServiceDto 로 변환되어야 한다.") {
                        val result = fieldMapper.toCreateDefaultReqServiceDto(dto)
                        result shouldBe expected

                        verify(exactly = 1) { attributeMapper.toAttributeCreateReqServiceDto(dto.attributes.first()) }
                    }
                }
            }

            describe("FieldMapper.toFieldDefault") {
                context("올바른 FieldCreateDefaultReqServiceDto 가 주어지면,") {
                    val dto = FieldCreateDefaultReqServiceDto(
                        name = "기본 필드",
                        icon = "default-icon",
                        type = "TEXT",
                        attributes = setOf(mockk<AttributeCreateReqServiceDto>()),
                    )

                    val expected = Field(
                        id = null,
                        externalId = null,
                        name = dto.name,
                        icon = dto.icon,
                        type = dto.type,
                        attributes = setOf(mockk<Attribute>()),
                        owner = null,
                        isDefault = true,
                    )

                    beforeEach {
                        every { attributeMapper.toAttribute(dto.attributes.first()) } returns expected.attributes.first()
                    }

                    it("isDefault가 true인 Field 엔티티로 변환되어야 한다.") {
                        val result = fieldMapper.toFieldDefault(dto)
                        result shouldBe expected
                    }
                }
            }

            describe("FieldMapper.toUpdateDefaultReqServiceDto") {
                context("올바른 FieldUpdateDefaultReqControllerDto, externalId 가 주어지면,") {
                    val dto = FieldUpdateDefaultReqControllerDto(
                        name = "Field Name",
                        icon = "field-icon",
                        attributes = setOf(mockk<AttributeUpdateReqControllerDto>()),
                    )

                    val expectedExternalId = UUID.randomUUID()

                    val expected = FieldUpdateDefaultReqServiceDto(
                        name = dto.name!!,
                        icon = dto.icon!!,
                        attributes = setOf(mockk<AttributeUpdateReqServiceDto>()),
                        externalId = expectedExternalId,
                    )

                    beforeEach {
                        every { attributeMapper.toAttributeUpdateReqServiceDto(dto.attributes.first()) } returns expected.attributes.first()
                    }

                    it("FieldUpdateReqServiceDto 로 변환되어야 한다.") {
                        val result = fieldMapper.toUpdateDefaultReqServiceDto(dto, expectedExternalId)
                        result shouldBe expected

                        verify(exactly = 1) { attributeMapper.toAttributeUpdateReqServiceDto(dto.attributes.first()) }
                    }
                }
            }

            describe("FieldMapper.updateFromDefaultDto") {
                context("모든 값이 들어있는 FieldUpdateDefaultReqServiceDto와 기존 Field 엔티티가 주어지면,") {
                    val dto = FieldUpdateDefaultReqServiceDto(
                        name = "Updated Default Field",
                        icon = "updated-default-icon",
                        externalId = UUID.randomUUID(),
                        attributes = setOf(mockk<AttributeUpdateReqServiceDto>()),
                    )

                    val existingField = Field(
                        name = "Old Default Field",
                        icon = "old-default-icon",
                        type = "TEXT",
                        isDefault = true,
                        attributes = setOf(mockk<Attribute>()),
                        owner = null,
                        externalId = "old-external-id",
                        id = null
                    )

                    val expected = Field(
                        name = dto.name!!, // name is updated
                        icon = dto.icon!!, // icon is updated
                        type = existingField.type,
                        isDefault = true,
                        attributes = existingField.attributes,
                        owner = existingField.owner,
                        externalId = existingField.externalId,
                        id = existingField.id
                    )

                    it("기존 Field 엔티티의 icon, name 필드가 업데이트되어야 한다.") {
                        val result = fieldMapper.updateFromDefaultDto(dto, existingField)
                        result shouldBe expected
                    }
                }

                context("name만 들어있는 FieldUpdateDefaultReqServiceDto가 주어지면,") {
                    val dto = FieldUpdateDefaultReqServiceDto(
                        name = "Updated Default Field",
                        icon = null,
                        externalId = UUID.randomUUID(),
                        attributes = setOf(),
                    )

                    val existingField = Field(
                        name = "Old Default Field",
                        icon = "old-default-icon",
                        type = "TEXT",
                        isDefault = true,
                        attributes = setOf(mockk<Attribute>()),
                        owner = null,
                        externalId = "old-external-id",
                        id = null
                    )

                    val expected = Field(
                        name = dto.name!!, // name is updated
                        icon = existingField.icon, // icon is not updated
                        type = existingField.type,
                        isDefault = true,
                        attributes = existingField.attributes,
                        owner = existingField.owner,
                        externalId = existingField.externalId,
                        id = existingField.id
                    )

                    it("기존 Field 엔티티의 name 필드만 업데이트되어야 한다.") {
                        val result = fieldMapper.updateFromDefaultDto(dto, existingField)
                        result shouldBe expected
                    }
                }

                context("icon만 들어있는 FieldUpdateDefaultReqServiceDto가 주어지면,") {
                    val dto = FieldUpdateDefaultReqServiceDto(
                        name = null,
                        icon = "new-default-icon",
                        externalId = UUID.randomUUID(),
                        attributes = setOf(),
                    )

                    val existingField = Field(
                        name = "Old Default Field",
                        icon = "old-default-icon",
                        type = "TEXT",
                        isDefault = true,
                        attributes = setOf(mockk<Attribute>()),
                        owner = null,
                        externalId = "old-external-id",
                        id = null
                    )

                    val expected = Field(
                        name = existingField.name, // name is not updated
                        icon = dto.icon!!, // icon is updated
                        type = existingField.type,
                        isDefault = true,
                        attributes = existingField.attributes,
                        owner = existingField.owner,
                        externalId = existingField.externalId,
                        id = existingField.id
                    )

                    it("기존 Field 엔티티의 icon 필드만 업데이트되어야 한다.") {
                        val result = fieldMapper.updateFromDefaultDto(dto, existingField)
                        result shouldBe expected
                    }
                }
            }
        }
    }
}