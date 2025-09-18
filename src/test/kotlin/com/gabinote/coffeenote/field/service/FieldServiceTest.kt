package com.gabinote.coffeenote.field.service

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotFound
import com.gabinote.coffeenote.common.util.exception.service.ResourceNotValid
import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.field.Field
import com.gabinote.coffeenote.field.domain.field.FieldRepository
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeFactory
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeUpdateReqServiceDto
import com.gabinote.coffeenote.field.dto.field.service.FieldCreateDefaultReqServiceDto
import com.gabinote.coffeenote.field.dto.field.service.FieldCreateReqServiceDto
import com.gabinote.coffeenote.field.dto.field.service.FieldResServiceDto
import com.gabinote.coffeenote.field.dto.field.service.FieldUpdateReqServiceDto
import com.gabinote.coffeenote.field.mapping.field.FieldMapper
import com.gabinote.coffeenote.field.service.attribute.AttributeService
import com.gabinote.coffeenote.field.service.field.FieldService
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.field.FieldTestDataHelper.createTestField
import com.gabinote.coffeenote.testSupport.testUtil.data.field.FieldTestDataHelper.createTestFieldUpdateReqServiceDto
import com.gabinote.coffeenote.testSupport.testUtil.page.TestPageableUtil
import com.gabinote.coffeenote.testSupport.testUtil.page.TestSliceUtil.toSlice
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows
import java.util.*

class FieldServiceTest : ServiceTestTemplate() {


    lateinit var fieldService: FieldService

    @MockK
    lateinit var fieldRepository: FieldRepository

    @MockK
    lateinit var fieldMapper: FieldMapper

    @MockK
    lateinit var fieldTypeFactory: FieldTypeFactory

    @MockK
    lateinit var attributeService: AttributeService


    init {
        beforeTest {
            clearAllMocks()
            fieldService = FieldService(
                fieldRepository = fieldRepository,
                fieldMapper = fieldMapper,
                fieldTypeFactory = fieldTypeFactory,
                attributeService = attributeService
            )
        }


        describe("[Field] FieldService") {
            describe("FieldService.fetchByExternalId") {
                context("존재하는 올바른 externalId가 주어지면,") {
                    val validExternalId = UUID.randomUUID()
                    val validField = createTestField()

                    beforeTest {
                        every { fieldRepository.findByExternalId(validExternalId.toString()) } returns validField
                    }


                    it("올바른 Field 엔티티를 반환해야 한다.") {
                        val res = fieldService.fetchByExternalId(validExternalId)
                        res shouldNotBe null

                        verify(exactly = 1) { fieldRepository.findByExternalId(validExternalId.toString()) }
                    }
                }

                context("존재하지 않는 잘못된 externalId가 주어지면,") {
                    val invalidExternalId = UUID.randomUUID()

                    beforeTest {
                        every { fieldRepository.findByExternalId(invalidExternalId.toString()) } returns null
                    }

                    it("ResourceNotFound exception이 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            fieldService.fetchByExternalId(invalidExternalId)
                        }
                        ex.name shouldBe "Field"
                        ex.identifier shouldBe invalidExternalId.toString()
                        ex.identifierType shouldBe "externalId"

                        verify(exactly = 1) { fieldRepository.findByExternalId(invalidExternalId.toString()) }
                    }
                }
            }

            describe("FieldService.getByExternalId") {
                context("존재하는 올바른 externalId가 주어지면,") {
                    val validExternalId = UUID.randomUUID()
                    val validField = createTestField()

                    beforeTest {
                        every { fieldRepository.findByExternalId(validExternalId.toString()) } returns validField
                    }


                    it("올바른 Field 엔티티를 반환해야 한다.") {
                        val res = fieldService.fetchByExternalId(validExternalId)
                        res shouldNotBe null

                        verify(exactly = 1) { fieldRepository.findByExternalId(validExternalId.toString()) }
                    }
                }

                context("존재하지 않는 잘못된 externalId가 주어지면,") {
                    val invalidExternalId = UUID.randomUUID()

                    beforeTest {
                        every { fieldRepository.findByExternalId(invalidExternalId.toString()) } returns null
                    }

                    it("ResourceNotFound exception이 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            fieldService.getByExternalId(invalidExternalId)
                        }

                        ex.name shouldBe "Field"
                        ex.identifier shouldBe invalidExternalId.toString()
                        ex.identifierType shouldBe "externalId"

                        verify(exactly = 1) { fieldRepository.findByExternalId(invalidExternalId.toString()) }
                    }
                }
            }

            describe("FieldService.getDefaultByExternalId") {
                context("존재하는 올바른 기본 field 의 externalId가 주어지면,") {
                    val validExternalId = UUID.randomUUID()
                    val validField = createTestField(default = true)
                    val expectedField = mockk<FieldResServiceDto>()

                    beforeTest {
                        every { fieldRepository.findByExternalId(validExternalId.toString()) } returns validField
                        every { fieldMapper.toResServiceDto(validField) } returns expectedField
                    }


                    it("올바른 Field 엔티티를 반환해야 한다.") {
                        val res = fieldService.getDefaultByExternalId(validExternalId)
                        res shouldNotBe null

                        verify(exactly = 1) {
                            fieldRepository.findByExternalId(validExternalId.toString())
                            fieldMapper.toResServiceDto(validField)
                        }
                    }
                }

                context("존재하지 않는 잘못된 externalId가 주어지면,") {
                    val invalidExternalId = UUID.randomUUID()

                    beforeTest {
                        every { fieldRepository.findByExternalId(invalidExternalId.toString()) } returns null
                    }

                    it("ResourceNotFound exception이 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            fieldService.getByExternalId(invalidExternalId)
                        }

                        ex.name shouldBe "Field"
                        ex.identifier shouldBe invalidExternalId.toString()
                        ex.identifierType shouldBe "externalId"

                        verify(exactly = 1) { fieldRepository.findByExternalId(invalidExternalId.toString()) }
                    }
                }

                context("존재하는 기본값이 아닌 field 의 externalId가 주어지면,") {
                    val invalidExternalId = UUID.randomUUID()
                    val invalidField = createTestField(default = false, externalId = invalidExternalId.toString())

                    beforeTest {
                        every { fieldRepository.findByExternalId(invalidExternalId.toString()) } returns invalidField
                    }

                    it("ResourceNotFound exception이 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            fieldService.getDefaultByExternalId(invalidExternalId)
                        }

                        ex.name shouldBe "Default Field"
                        ex.identifier shouldBe invalidExternalId.toString()
                        ex.identifierType shouldBe "externalId"

                        verify(exactly = 1) {
                            fieldRepository.findByExternalId(invalidExternalId.toString())
                        }
                    }
                }

            }

            describe("FieldService.getOwnedByExternalId") {
                context("존재하는 올바른 자기 소유의 field 의 externalId가 주어지면,") {
                    val fieldOwner = UUID.randomUUID().toString()
                    val validExternalId = UUID.randomUUID()
                    val validField = createTestField(externalId = validExternalId.toString(), owner = fieldOwner)
                    val expectedField = mockk<FieldResServiceDto>()

                    beforeTest {
                        every { fieldRepository.findByExternalId(validExternalId.toString()) } returns validField
                        every { fieldMapper.toResServiceDto(validField) } returns expectedField
                    }


                    it("올바른 Field 엔티티를 반환해야 한다.") {
                        val res = fieldService.getOwnedByExternalId(externalId = validExternalId, executor = fieldOwner)
                        res shouldNotBe null

                        verify(exactly = 1) {
                            fieldRepository.findByExternalId(validExternalId.toString())
                            fieldMapper.toResServiceDto(validField)
                        }
                    }
                }

                context("존재하지 않는 잘못된 externalId가 주어지면,") {
                    val fieldOwner = UUID.randomUUID().toString()
                    val invalidExternalId = UUID.randomUUID()

                    beforeTest {
                        every { fieldRepository.findByExternalId(invalidExternalId.toString()) } returns null
                    }

                    it("ResourceNotFound exception이 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            fieldService.getOwnedByExternalId(externalId = invalidExternalId, executor = fieldOwner)
                        }

                        ex.name shouldBe "Field"
                        ex.identifier shouldBe invalidExternalId.toString()
                        ex.identifierType shouldBe "externalId"

                        verify(exactly = 1) { fieldRepository.findByExternalId(invalidExternalId.toString()) }
                    }
                }

                context("존재하는 자기 소유가 아닌 아닌 field 의 externalId가 주어지면,") {
                    val executor = UUID.randomUUID().toString()
                    val invalidExternalId = UUID.randomUUID()
                    val invalidField = createTestField(externalId = invalidExternalId.toString(), owner = "other owner")

                    beforeTest {
                        every { fieldRepository.findByExternalId(invalidExternalId.toString()) } returns invalidField
                    }

                    it("ResourceNotFound exception이 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            fieldService.getOwnedByExternalId(externalId = invalidExternalId, executor = executor)
                        }

                        ex.name shouldBe "Owned Field"
                        ex.identifier shouldBe invalidExternalId.toString()
                        ex.identifierType shouldBe "externalId"

                        verify(exactly = 1) {
                            fieldRepository.findByExternalId(invalidExternalId.toString())
                        }
                    }
                }

            }

            describe("FieldService.getAll") {
                context("모든 field를 조회하면,") {
                    val pageable = TestPageableUtil.createPageable()
                    val fields = listOf(createTestField()).toSlice(pageable)
                    val expected = listOf(mockk<FieldResServiceDto>()).toSlice(pageable)

                    beforeTest {
                        every { fieldRepository.findAllBy(pageable) } returns fields
                        every { fieldMapper.toResServiceDto(fields.toList()[0]) } returns expected.toList()[0]
                    }

                    it("모든 Field 리스트를 반환해야 한다.") {
                        val res = fieldService.getAll(pageable)
                        res shouldNotBe null
                        res.content.size shouldBe 1
                        res.content[0] shouldBe expected.toList()[0]

                        verify(exactly = 1) {
                            fieldRepository.findAllBy(pageable)
                            fieldMapper.toResServiceDto(fields.toList()[0])
                        }
                    }
                }

            }

            describe("FieldService.getAllDefault") {
                context("모든 기본 field를 조회하면,") {
                    val pageable = TestPageableUtil.createPageable()
                    val fields = listOf(createTestField()).toSlice(pageable)
                    val expected = listOf(mockk<FieldResServiceDto>()).toSlice(pageable)

                    beforeTest {
                        every { fieldRepository.findAllByIsDefault(true, pageable) } returns fields
                        every { fieldMapper.toResServiceDto(fields.toList()[0]) } returns expected.toList()[0]
                    }

                    it("모든 기본 Field 리스트를 반환해야 한다.") {
                        val res = fieldService.getAllDefault(pageable)
                        res shouldNotBe null
                        res.content.size shouldBe 1
                        res.content[0] shouldBe expected.toList()[0]

                        verify(exactly = 1) {
                            fieldRepository.findAllByIsDefault(true, pageable)
                            fieldMapper.toResServiceDto(fields.toList()[0])
                        }
                    }
                }


            }

            describe("FieldService.getAllOwned") {

                context("모든 자기 소유의 field를 조회하면,") {
                    val fieldOwner = UUID.randomUUID().toString()
                    val pageable = TestPageableUtil.createPageable()
                    val fields = listOf(mockk<Field>()).toSlice(pageable)
                    val expected = listOf(mockk<FieldResServiceDto>()).toSlice(pageable)

                    beforeTest {
                        every { fieldRepository.findAllByOwner(fieldOwner, pageable) } returns fields
                        every { fieldMapper.toResServiceDto(fields.toList()[0]) } returns expected.toList()[0]
                    }

                    it("모든 자기 소유의 Field 리스트를 반환해야 한다.") {
                        val res = fieldService.getAllOwned(executor = fieldOwner, pageable = pageable)
                        res shouldNotBe null
                        res.content.size shouldBe 1
                        res.content[0] shouldBe expected.toList()[0]

                        verify(exactly = 1) {
                            fieldRepository.findAllByOwner(fieldOwner, pageable)
                            fieldMapper.toResServiceDto(fields.toList()[0])
                        }
                    }
                }

            }

            describe("FieldService.getAllOwnedOrDefault") {
                context("모든 자기 소유 또는 기본 field를 조회하면,") {
                    val fieldOwner = UUID.randomUUID().toString()
                    val pageable = TestPageableUtil.createPageable()
                    val fields = listOf(mockk<Field>()).toSlice(pageable)
                    val expected = listOf(mockk<FieldResServiceDto>()).toSlice(pageable)

                    beforeTest {
                        every { fieldRepository.findAllByIsDefaultOrOwner(true, fieldOwner, pageable) } returns fields
                        every { fieldMapper.toResServiceDto(fields.toList()[0]) } returns expected.toList()[0]
                    }

                    it("모든 자기 소유 또는 기본 Field 리스트를 반환해야 한다.") {
                        val res = fieldService.getAllOwnedOrDefault(executor = fieldOwner, pageable = pageable)
                        res shouldNotBe null
                        res.content.size shouldBe 1
                        res.content[0] shouldBe expected.toList()[0]

                        verify(exactly = 1) {
                            fieldRepository.findAllByIsDefaultOrOwner(true, fieldOwner, pageable)
                            fieldMapper.toResServiceDto(fields.toList()[0])
                        }
                    }
                }

            }

            describe("FieldService.deleteByExternalId") {
                context("올바른 externalId가 주어지면,") {
                    val validExternalId = UUID.randomUUID()
                    val validField = mockk<Field>()
                    val deletedFields = listOf(validField)

                    beforeTest {
                        every { fieldRepository.deleteByExternalId(validExternalId.toString()) } returns deletedFields
                    }

                    it("해당 Field 엔티티를 삭제한다.") {
                        fieldService.deleteByExternalId(validExternalId)

                        verify(exactly = 1) {
                            fieldRepository.deleteByExternalId(validExternalId.toString())
                        }
                    }
                }

                context("잘못된 externalId가 주어지면,") {
                    val invalidExternalId = UUID.randomUUID()
                    val deletedFields = emptyList<Field>()

                    beforeTest {
                        every { fieldRepository.deleteByExternalId(invalidExternalId.toString()) } returns deletedFields
                    }

                    it("ResourceNotFound exception이 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            fieldService.deleteByExternalId(invalidExternalId)
                        }

                        ex.name shouldBe "Field"
                        ex.identifier shouldBe invalidExternalId.toString()
                        ex.identifierType shouldBe "externalId"

                        verify(exactly = 1) {
                            fieldRepository.deleteByExternalId(invalidExternalId.toString())
                        }
                    }
                }
            }

            describe("FieldService.deleteDefaultByExternalId") {
                context("올바른 기본 field의 externalId가 주어지면,") {
                    val validExternalId = UUID.randomUUID()
                    val validField = createTestField(default = true)
                    beforeTest {
                        every { fieldRepository.findByExternalId(validExternalId.toString()) } returns validField
                        every { fieldRepository.delete(validField) } returns Unit
                    }

                    it("해당 기본 Field 엔티티를 삭제한다.") {
                        fieldService.deleteDefaultByExternalId(validExternalId)


                        verify(exactly = 1) {
                            fieldRepository.findByExternalId(validExternalId.toString())
                            fieldRepository.delete(validField)
                        }
                    }
                }

                context("잘못된 externalId가 주어지면,") {
                    val invalidExternalId = UUID.randomUUID()

                    beforeTest {
                        every { fieldRepository.findByExternalId(invalidExternalId.toString()) } returns null
                    }

                    it("ResourceNotFound exception이 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            fieldService.deleteDefaultByExternalId(invalidExternalId)
                        }

                        ex.name shouldBe "Field"
                        ex.identifier shouldBe invalidExternalId.toString()
                        ex.identifierType shouldBe "externalId"

                        verify(exactly = 1) {
                            fieldRepository.findByExternalId(invalidExternalId.toString())
                        }
                    }
                }

                context("기본값이 아닌 field의 externalId가 주어지면,") {
                    val invalidExternalId = UUID.randomUUID()
                    val invalidField = createTestField(default = false, externalId = invalidExternalId.toString())

                    beforeTest {
                        every { fieldRepository.findByExternalId(invalidExternalId.toString()) } returns invalidField
                    }

                    it("ResourceNotFound exception이 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            fieldService.deleteDefaultByExternalId(invalidExternalId)
                        }

                        ex.name shouldBe "Default Field"
                        ex.identifier shouldBe invalidExternalId.toString()
                        ex.identifierType shouldBe "externalId"

                        verify(exactly = 1) {
                            fieldRepository.findByExternalId(invalidExternalId.toString())
                        }
                    }
                }
            }
            describe("FieldService.deleteOwnedByExternalId") {
                context("올바른 자기 소유의 field externalId가 주어지면,") {
                    val fieldOwner = UUID.randomUUID().toString()
                    val validExternalId = UUID.randomUUID()
                    val validField =
                        createTestField(default = true, externalId = validExternalId.toString(), owner = fieldOwner)

                    beforeTest {
                        every { fieldRepository.findByExternalId(validExternalId.toString()) } returns validField
                        every { fieldRepository.delete(validField) } returns Unit
                    }

                    it("해당 자기 소유의 Field 엔티티를 삭제한다.") {
                        fieldService.deleteOwnedByExternalId(externalId = validExternalId, executor = fieldOwner)


                        verify(exactly = 1) {
                            fieldRepository.findByExternalId(validExternalId.toString())
                            fieldRepository.delete(validField)
                        }
                    }
                }

                context("잘못된 externalId가 주어지면,") {
                    val fieldOwner = UUID.randomUUID().toString()
                    val invalidExternalId = UUID.randomUUID()

                    beforeTest {
                        every { fieldRepository.findByExternalId(invalidExternalId.toString()) } returns null
                    }

                    it("ResourceNotFound exception이 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            fieldService.deleteOwnedByExternalId(externalId = invalidExternalId, executor = fieldOwner)
                        }

                        ex.name shouldBe "Field"
                        ex.identifier shouldBe invalidExternalId.toString()
                        ex.identifierType shouldBe "externalId"

                        verify(exactly = 1) {
                            fieldRepository.findByExternalId(invalidExternalId.toString())
                        }
                    }
                }

                context("자기 소유가 아닌 아닌 field externalId가 주어지면,") {
                    val executor = UUID.randomUUID().toString()
                    val invalidExternalId = UUID.randomUUID()
                    val invalidField = createTestField(default = false, externalId = invalidExternalId.toString())

                    beforeTest {
                        every { fieldRepository.findByExternalId(invalidExternalId.toString()) } returns invalidField
                    }

                    it("ResourceNotFound exception이 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            fieldService.deleteOwnedByExternalId(externalId = invalidExternalId, executor = executor)
                        }
                        ex.name shouldBe "Owned Field"
                        ex.identifier shouldBe invalidExternalId.toString()
                        ex.identifierType shouldBe "externalId"
                        verify(exactly = 1) {
                            fieldRepository.findByExternalId(invalidExternalId.toString())
                        }
                    }
                }
            }

            describe("FieldService.createOwnedField") {

                context("올바른 생성 정보가 주어지면,") {

                    val validDto = mockk<FieldCreateReqServiceDto>()
                    val attributeReq = mockk<AttributeCreateReqServiceDto>()
                    val newField = mockk<Field>()
                    beforeTest {
                        every { fieldMapper.toField(validDto) } returns newField
                    }
                    // type = TEST
                    val testType = mockk<FieldType>()

                    beforeTest {
                        every { validDto.type } returns testType
                    }


                    //attribute 검증
                    val attribute = mockk<Attribute>()

                    beforeTest {
                        every { validDto.attributes } returns setOf(attributeReq)
                        every {
                            attributeService.createAttribute(
                                fieldType = testType,
                                attributesCreateReq = setOf(attributeReq)
                            )
                        } returns setOf(
                            attribute
                        )
                        every { newField.changeAttributes(setOf(attribute)) } returns Unit
                    }
                    // 저장
                    val savedField = mockk<Field>()

                    beforeTest {
                        every { fieldRepository.save(newField) } returns savedField
                    }

                    val expected = mockk<FieldResServiceDto>()

                    beforeTest {
                        every { fieldMapper.toResServiceDto(savedField) } returns expected
                    }



                    it("새로운 자기 소유의 Field를 생성하여 반환해야 한다.") {
                        val res = fieldService.createOwnedField(validDto)
                        res shouldNotBe null
                        res shouldBe expected

                        verify(exactly = 1) {
                            fieldMapper.toField(validDto)
                            validDto.type
                            validDto.attributes
                            attributeService.createAttribute(
                                fieldType = testType,
                                attributesCreateReq = setOf(attributeReq)
                            )
                            newField.changeAttributes(setOf(attribute))
                            fieldRepository.save(newField)
                            fieldMapper.toResServiceDto(savedField)
                        }
                    }
                }

                context("잘못된 Attribute를 가진 정보가 주어지면,") {

                    val validDto = mockk<FieldCreateReqServiceDto>()
                    val attributeReq = mockk<AttributeCreateReqServiceDto>()
                    val newField = mockk<Field>()
                    beforeTest {
                        every { fieldMapper.toField(validDto) } returns newField
                    }

                    // type = TEST
                    val testType = mockk<FieldType>()

                    beforeTest {
                        every { validDto.type } returns testType
                    }

                    beforeTest {
                        every { validDto.attributes } returns setOf(attributeReq)
                        every {
                            attributeService.createAttribute(
                                fieldType = testType,
                                attributesCreateReq = setOf(attributeReq)
                            )
                        } throws ResourceNotValid(name = "Field Attribute", reasons = listOf("Invalid attribute"))
                    }

                    it("ResourceNotValid 예외가 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotValid> {
                            fieldService.createOwnedField(validDto)
                        }

                        verify(exactly = 1) {
                            fieldMapper.toField(validDto)
                            validDto.type
                            validDto.attributes
                            attributeService.createAttribute(
                                fieldType = testType,
                                attributesCreateReq = setOf(attributeReq)
                            )
                        }
                    }
                }

            }

            describe("FieldService.updateOwnedField") {

                context("올바른 수정 정보가 주어지면,") {
                    val validDto = mockk<FieldUpdateReqServiceDto>()
                    val externalId = UUID.randomUUID()
                    val existingField = mockk<Field>()
                    beforeTest {
                        every { validDto.externalId } returns externalId
                        every { fieldRepository.findByExternalId(externalId.toString()) } returns existingField
                    }

                    // checkOwnerShop
                    val executor = UUID.randomUUID().toString()
                    beforeTest {
                        every { validDto.owner } returns executor
                        every { existingField.isOwner(executor) } returns true
                    }

                    // update
                    beforeTest {
                        every { fieldMapper.updateFromDto(validDto, existingField) } returns existingField
                    }

                    //updateAttributesIfNeeded()

                    val newAttributeReq = mockk<AttributeUpdateReqServiceDto>()
                    val testType = mockk<FieldType>()
                    beforeTest {
                        every { existingField.type } returns "TEST"
                        every { fieldTypeFactory.getFieldType("TEST") } returns testType
                    }

                    val beforeAttribute = mockk<Attribute>()
                    val newAttribute = mockk<Attribute>()

                    beforeTest {
                        every { validDto.attributes } returns setOf(newAttributeReq)
                        every { existingField.attributes } returns setOf(beforeAttribute)
                        every {
                            attributeService.updateAttribute(
                                fieldType = testType,
                                oldAttributes = setOf(beforeAttribute),
                                newAttributeReq = setOf(newAttributeReq)
                            )
                        } returns setOf(newAttribute)
                    }

                    //save
                    val savedField = mockk<Field>()
                    beforeTest {
                        every { fieldRepository.save(existingField) } returns savedField
                    }

                    val expected = mockk<FieldResServiceDto>()
                    beforeTest {
                        every { fieldMapper.toResServiceDto(savedField) } returns expected
                    }

                    it("자기 소유의 Field를 수정하여 반환해야 한다.") {
                        val res = fieldService.updateOwnedField(dto = validDto)
                        res shouldNotBe null
                        res shouldBe expected

                        verify {
                            validDto.externalId
                            fieldRepository.findByExternalId(externalId.toString())
                            validDto.owner
                            existingField.isOwner(executor)
                            fieldMapper.updateFromDto(validDto, existingField)
                            existingField.type
                            fieldTypeFactory.getFieldType("TEST")
                            validDto.attributes
                            existingField.attributes
                            attributeService.updateAttribute(
                                fieldType = testType,
                                oldAttributes = setOf(beforeAttribute),
                                newAttributeReq = setOf(newAttributeReq)
                            )
                            fieldRepository.save(existingField)
                            fieldMapper.toResServiceDto(savedField)
                        }
                    }
                }

                context("잘못된 attribute를 가진  수정 정보가 주어지면,") {
                    val invalidDto = mockk<FieldUpdateReqServiceDto>()
                    val externalId = UUID.randomUUID()
                    val existingField = mockk<Field>()
                    beforeTest {
                        every { invalidDto.externalId } returns externalId
                        every { fieldRepository.findByExternalId(externalId.toString()) } returns existingField
                    }

                    // checkOwnerShop
                    val executor = UUID.randomUUID().toString()
                    beforeTest {
                        every { invalidDto.owner } returns executor
                        every { existingField.isOwner(executor) } returns true
                    }

                    // update
                    beforeTest {
                        every { fieldMapper.updateFromDto(invalidDto, existingField) } returns existingField
                    }

                    //updateAttributesIfNeeded()
                    val newAttributeReq = mockk<AttributeUpdateReqServiceDto>()
                    val testType = mockk<FieldType>()
                    beforeTest {
                        every { existingField.type } returns "TEST"
                        every { fieldTypeFactory.getFieldType("TEST") } returns testType
                    }

                    val beforeAttribute = mockk<Attribute>()
                    val newAttribute = mockk<Attribute>()

                    beforeTest {
                        every { invalidDto.attributes } returns setOf(newAttributeReq)
                        every { existingField.attributes } returns setOf(beforeAttribute)
                        every {
                            attributeService.updateAttribute(
                                fieldType = testType,
                                oldAttributes = setOf(beforeAttribute),
                                newAttributeReq = setOf(newAttributeReq)
                            )
                        } throws ResourceNotValid(
                            name = "Field Attribute",
                            reasons = listOf("Invalid attribute")
                        )
                    }


                    it("ResourceNotValid 예외가 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotValid> {
                            fieldService.updateOwnedField(dto = invalidDto)
                        }

                        verify {
                            invalidDto.externalId
                            fieldRepository.findByExternalId(externalId.toString())
                            invalidDto.owner
                            existingField.isOwner(executor)
                            fieldMapper.updateFromDto(invalidDto, existingField)
                            existingField.type
                            fieldTypeFactory.getFieldType("TEST")
                            invalidDto.attributes
                            existingField.attributes
                            attributeService.updateAttribute(
                                fieldType = testType,
                                oldAttributes = setOf(beforeAttribute),
                                newAttributeReq = setOf(newAttributeReq)
                            )
                        }
                    }
                }

                context("자신의 소유가 아닌 field 수정 정보가 주어지면,") {
                    val invalidDto = mockk<FieldUpdateReqServiceDto>()
                    val externalId = UUID.randomUUID()
                    val existingField = mockk<Field>()
                    beforeTest {
                        every { invalidDto.externalId } returns externalId
                        every { fieldRepository.findByExternalId(externalId.toString()) } returns existingField
                    }

                    // checkOwnerShop
                    val executor = UUID.randomUUID().toString()
                    beforeTest {
                        every { invalidDto.owner } returns executor
                        every { existingField.isOwner(executor) } returns false
                        every { existingField.externalId } returns externalId.toString()
                    }

                    it("ResourceNotFound 예외가 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            fieldService.updateOwnedField(dto = invalidDto)
                        }

                        ex.name shouldBe "Owned Field"
                        ex.identifier shouldBe externalId.toString()
                        ex.identifierType shouldBe "externalId"


                        verify {
                            invalidDto.externalId
                            fieldRepository.findByExternalId(externalId.toString())
                            invalidDto.owner
                            existingField.isOwner(executor)
                            existingField.externalId
                        }
                    }
                }

            }


            describe("FieldService.createDefaultField") {

                context("올바른 생성 정보가 주어지면,") {

                    val validDto = mockk<FieldCreateDefaultReqServiceDto>()
                    val attributeReq = mockk<AttributeCreateReqServiceDto>()
                    val newField = mockk<Field>()
                    beforeTest {
                        every { fieldMapper.toFieldDefault(validDto) } returns newField
                    }
                    // type = TEST
                    val testType = mockk<FieldType>()

                    beforeTest {
                        every { validDto.type } returns testType
                    }


                    //attribute 검증
                    val attribute = mockk<Attribute>()

                    beforeTest {
                        every { validDto.attributes } returns setOf(attributeReq)
                        every {
                            attributeService.createAttribute(
                                fieldType = testType,
                                attributesCreateReq = setOf(attributeReq)
                            )
                        } returns setOf(
                            attribute
                        )
                        every { newField.changeAttributes(setOf(attribute)) } returns Unit
                    }


                    // 저장
                    val savedField = mockk<Field>()

                    beforeTest {
                        every { fieldRepository.save(newField) } returns savedField
                    }

                    val expected = mockk<FieldResServiceDto>()

                    beforeTest {
                        every { fieldMapper.toResServiceDto(savedField) } returns expected
                    }



                    it("새로운 기본값 Field를 생성하여 반환해야 한다.") {
                        val res = fieldService.createDefaultField(validDto)
                        res shouldNotBe null
                        res shouldBe expected

                        verify(exactly = 1) {
                            fieldMapper.toFieldDefault(validDto)
                            validDto.type
                            validDto.attributes
                            attributeService.createAttribute(
                                fieldType = testType,
                                attributesCreateReq = setOf(attributeReq)
                            )
                            newField.changeAttributes(setOf(attribute))
                            fieldRepository.save(newField)
                            fieldMapper.toResServiceDto(savedField)
                        }
                    }
                }

                context("잘못된 Attribute를 가진 정보가 주어지면,") {

                    val validDto = mockk<FieldCreateDefaultReqServiceDto>()
                    val attributeReq = mockk<AttributeCreateReqServiceDto>()
                    val newField = mockk<Field>()
                    beforeTest {
                        every { fieldMapper.toFieldDefault(validDto) } returns newField
                    }

                    // type = TEST
                    val testType = mockk<FieldType>()

                    beforeTest {
                        every { validDto.type } returns testType
                    }


                    //attribute 검증
                    beforeTest {
                        every { validDto.attributes } returns setOf(attributeReq)
                        every {
                            attributeService.createAttribute(
                                fieldType = testType,
                                attributesCreateReq = setOf(attributeReq)
                            )
                        } throws ResourceNotValid(
                            name = "Field Attribute",
                            reasons = listOf("Invalid attribute")
                        )

                    }

                    it("ResourceNotValid 예외가 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotValid> {
                            fieldService.createDefaultField(validDto)
                        }
                        verify(exactly = 1) {
                            fieldMapper.toFieldDefault(validDto)
                            validDto.type
                            validDto.attributes
                            attributeService.createAttribute(
                                fieldType = testType,
                                attributesCreateReq = setOf(attributeReq)
                            )
                        }
                    }
                }
            }

            describe("FieldService.updateDefaultField") {

                context("올바른 수정 정보가 주어지면,") {

                    val externalId = UUID.randomUUID()
                    val attributeUpdateReq = AttributeUpdateReqServiceDto(
                        key = "TEST",
                        value = setOf("NEW")
                    )
                    val validDto = createTestFieldUpdateReqServiceDto(
                        externalId = externalId,
                        attributes = setOf(attributeUpdateReq)
                    )
                    val beforeAttribute = mockk<Attribute>()
                    val existingField =
                        createTestField(default = true, type = "TEST", attributes = setOf(beforeAttribute))
                    beforeTest {
                        every { fieldRepository.findByExternalId(externalId.toString()) } returns existingField
                    }

                    // update
                    beforeTest {
                        every { fieldMapper.updateFromDefaultDto(validDto, existingField) } returns existingField
                    }

                    //updateAttributesIfNeeded()
                    val testType = mockk<FieldType>()
                    beforeTest {
                        every { fieldTypeFactory.getFieldType("TEST") } returns testType
                    }


                    val newAttribute = mockk<Attribute>()

                    beforeTest {

                        every {
                            attributeService.updateAttribute(
                                fieldType = testType,
                                oldAttributes = setOf(beforeAttribute),
                                newAttributeReq = setOf(attributeUpdateReq)
                            )
                        } returns setOf(newAttribute)
                    }

                    //save
                    val savedField = mockk<Field>()
                    beforeTest {
                        every { fieldRepository.save(existingField) } returns savedField
                    }

                    val expected = mockk<FieldResServiceDto>()
                    beforeTest {
                        every { fieldMapper.toResServiceDto(savedField) } returns expected
                    }

                    it("기본값 Field를 수정하여 반환해야 한다.") {
                        val res = fieldService.updateDefaultField(dto = validDto)
                        res shouldNotBe null
                        res shouldBe expected

                        verify {
                            fieldRepository.findByExternalId(externalId.toString())
                            fieldMapper.updateFromDefaultDto(validDto, existingField)
                            fieldTypeFactory.getFieldType("TEST")
                            attributeService.updateAttribute(
                                fieldType = testType,
                                oldAttributes = setOf(beforeAttribute),
                                newAttributeReq = setOf(attributeUpdateReq)
                            )
                            fieldRepository.save(existingField)
                            fieldMapper.toResServiceDto(savedField)

                        }
                    }
                }

                context("잘못된 attribute를 가진  수정 정보가 주어지면,") {
                    val invalidAttributeReq = AttributeUpdateReqServiceDto(
                        key = "TEST",
                        value = setOf("NEW")
                    )

                    val externalId = UUID.randomUUID()
                    val invalidDto = createTestFieldUpdateReqServiceDto(
                        externalId = externalId,
                        attributes = setOf(invalidAttributeReq)
                    )
                    val beforeAttribute = mockk<Attribute>()
                    val existingField =
                        createTestField(default = true, type = "TEST", attributes = setOf(beforeAttribute))
                    beforeTest {
                        every { fieldRepository.findByExternalId(externalId.toString()) } returns existingField
                    }

                    // checkOwnerShop

                    // update
                    beforeTest {
                        every { fieldMapper.updateFromDefaultDto(invalidDto, existingField) } returns existingField
                    }

                    //updateAttributesIfNeeded()
                    val testType = mockk<FieldType>()
                    beforeTest {
                        every { fieldTypeFactory.getFieldType("TEST") } returns testType
                    }


                    beforeTest {
                        every {
                            attributeService.updateAttribute(
                                fieldType = testType,
                                oldAttributes = setOf(beforeAttribute),
                                newAttributeReq = setOf(invalidAttributeReq)
                            )
                        } throws ResourceNotValid(name = "Field Attribute", reasons = listOf("Invalid attribute"))
                    }


                    it("ResourceNotValid 예외가 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotValid> {
                            fieldService.updateDefaultField(dto = invalidDto)
                        }


                        verify {
                            invalidDto.externalId
                            fieldRepository.findByExternalId(externalId.toString())
                            fieldMapper.updateFromDefaultDto(invalidDto, existingField)
                            existingField.type
                            fieldTypeFactory.getFieldType("TEST")
                            invalidDto.attributes
                            existingField.attributes
                            attributeService.updateAttribute(
                                fieldType = testType,
                                oldAttributes = setOf(beforeAttribute),
                                newAttributeReq = setOf(invalidAttributeReq)
                            )

                        }
                    }
                }

                context("기본 값이 아닌 field 수정 정보가 주어지면,") {

                    val externalId = UUID.randomUUID()
                    val invalidDto = createTestFieldUpdateReqServiceDto(externalId = externalId)
                    val existingField =
                        createTestField(externalId = externalId.toString(), default = false, type = "TEST")
                    beforeTest {
                        every { fieldRepository.findByExternalId(externalId.toString()) } returns existingField
                    }

                    it("ResourceNotFound 예외가 발생해야 한다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            fieldService.updateDefaultField(dto = invalidDto)
                        }

                        ex.name shouldBe "Default Field"
                        ex.identifier shouldBe externalId.toString()
                        ex.identifierType shouldBe "externalId"


                        verify {
                            invalidDto.externalId
                            fieldRepository.findByExternalId(externalId.toString())
                            existingField.isDefault
                            existingField.externalId
                        }
                    }
                }

            }
        }
    }
}