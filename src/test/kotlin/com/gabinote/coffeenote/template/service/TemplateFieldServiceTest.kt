package com.gabinote.coffeenote.template.service

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotValid
import com.gabinote.coffeenote.field.service.attribute.AttributeService
import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldCreateReqServiceDto
import com.gabinote.coffeenote.template.mapping.templateField.TemplateFieldMapper
import com.gabinote.coffeenote.template.service.templateField.TemplateFieldService
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.field.AttributeTestDataHelper.createTestAttribute
import com.gabinote.coffeenote.testSupport.testUtil.data.field.AttributeTestDataHelper.createTestAttributeCreateReqServiceDto
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldType
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldTypeCantDisplay
import com.gabinote.coffeenote.testSupport.testUtil.data.template.TemplateFieldDataHelper.createTestTemplateField
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.assertThrows

class TemplateFieldServiceTest : ServiceTestTemplate() {
    private lateinit var templateFieldService: TemplateFieldService

    @MockK
    private lateinit var attributeService: AttributeService

    @MockK
    private lateinit var templateFieldMapper: TemplateFieldMapper

    init {
        beforeTest {
            clearAllMocks()
            templateFieldService = TemplateFieldService(
                attributeService = attributeService,
                templateFieldMapper = templateFieldMapper
            )
        }

        describe("[Template] TemplateFieldService") {
            describe("TemplateFieldService.create") {
                context("올바른 생성 요청이 들어오면") {
                    val validDto1 = TemplateFieldCreateReqServiceDto(
                        id = "field1",
                        name = "Field 1",
                        icon = "icon1",
                        type = TestFieldType,
                        order = 1,
                        isDisplay = true,
                        attributes = setOf(
                            createTestAttributeCreateReqServiceDto(
                                key = "isValid",
                                value = setOf("true")
                            )
                        )
                    )
                    val validDto2 = TemplateFieldCreateReqServiceDto(
                        id = "field2",
                        name = "Field 2",
                        icon = "icon2",
                        type = TestFieldType,
                        order = 2,
                        isDisplay = false,
                        attributes = setOf(createTestAttributeCreateReqServiceDto(key = "isValid", value = setOf("ok")))
                    )
                    val dtoList = listOf(validDto1, validDto2)

                    val templateField1 = createTestTemplateField(id = "field1", order = 1)
                    val templateField2 = createTestTemplateField(id = "field2", order = 2)
                    val attribute1 = createTestAttribute(key = "isValid", value = setOf("true"))
                    val attribute2 = createTestAttribute(key = "isValid", value = setOf("ok"))

                    beforeTest {
                        every { templateFieldMapper.toTemplateField(validDto1) } returns templateField1
                        every { templateFieldMapper.toTemplateField(validDto2) } returns templateField2
                        every {
                            attributeService.createAttribute(
                                fieldType = TestFieldType,
                                attributesCreateReq = validDto1.attributes
                            )
                        } returns setOf(attribute1)
                        every {
                            attributeService.createAttribute(
                                fieldType = TestFieldType,
                                attributesCreateReq = validDto2.attributes
                            )
                        } returns setOf(attribute2)
                    }

                    it("TemplateField 리스트를 리턴한다") {
                        val result = templateFieldService.create(dtoList)

                        result shouldNotBe null
                        result.size shouldBe 2
                        result[0] shouldBe templateField1
                        result[1] shouldBe templateField2

                        verify(exactly = 1) {
                            templateFieldMapper.toTemplateField(validDto1)
                            templateFieldMapper.toTemplateField(validDto2)
                            attributeService.createAttribute(
                                fieldType = TestFieldType,
                                attributesCreateReq = validDto1.attributes
                            )
                            attributeService.createAttribute(
                                fieldType = TestFieldType,
                                attributesCreateReq = validDto2.attributes
                            )
                        }
                    }
                }

                describe("잘못된 요청이 들어오면 실패한다.") {
                    context("order 값이 중복되면") {
                        val duplicateOrderDto1 = TemplateFieldCreateReqServiceDto(
                            id = "field1",
                            name = "Field 1",
                            icon = "icon1",
                            type = TestFieldType,
                            order = 1,
                            isDisplay = true,
                            attributes = setOf(createTestAttributeCreateReqServiceDto())
                        )
                        val duplicateOrderDto2 = TemplateFieldCreateReqServiceDto(
                            id = "field2",
                            name = "Field 2",
                            icon = "icon2",
                            type = TestFieldType,
                            order = 1, // 중복된 order
                            isDisplay = false,
                            attributes = setOf(createTestAttributeCreateReqServiceDto())
                        )
                        val duplicateOrderDtoList = listOf(duplicateOrderDto1, duplicateOrderDto2)

                        it("ResourceNotValid 예외를 던진다") {
                            val ex = assertThrows<ResourceNotValid> {
                                templateFieldService.create(duplicateOrderDtoList)
                            }
                            ex.name shouldBe "TemplateField"
                            ex.reasons shouldBe listOf("Duplicate order values are not allowed.")
                        }
                    }

                    context("attributes 가 올바르지 않으면") {
                        val invalidAttributeDto = TemplateFieldCreateReqServiceDto(
                            id = "field1",
                            name = "Field 1",
                            icon = "icon1",
                            type = TestFieldType,
                            order = 1,
                            isDisplay = true,
                            attributes = setOf(
                                createTestAttributeCreateReqServiceDto(
                                    key = "isValid",
                                    value = setOf("invalid")
                                )
                            )
                        )
                        val invalidDtoList = listOf(invalidAttributeDto)
                        val templateField = createTestTemplateField(id = "field1")

                        beforeTest {
                            every { templateFieldMapper.toTemplateField(invalidAttributeDto) } returns templateField
                            every {
                                attributeService.createAttribute(
                                    fieldType = TestFieldType,
                                    attributesCreateReq = invalidAttributeDto.attributes
                                )
                            } throws ResourceNotValid(name = "Attribute", reasons = listOf("not valid attribute"))
                        }

                        it("ResourceNotValid 예외를 던진다") {
                            val ex = assertThrows<ResourceNotValid> {
                                templateFieldService.create(invalidDtoList)
                            }
                            ex.name shouldBe "Attribute"
                            ex.reasons shouldBe listOf("not valid attribute")

                            verify(exactly = 1) {
                                templateFieldMapper.toTemplateField(invalidAttributeDto)
                                attributeService.createAttribute(
                                    fieldType = TestFieldType,
                                    attributesCreateReq = invalidAttributeDto.attributes
                                )
                            }
                        }
                    }

                    context("order 값이 1부터 연속적이지 않으면") {
                        val nonSequentialDto1 = TemplateFieldCreateReqServiceDto(
                            id = "field1",
                            name = "Field 1",
                            icon = "icon1",
                            type = TestFieldType,
                            order = 1,
                            isDisplay = true,
                            attributes = setOf(createTestAttributeCreateReqServiceDto())
                        )
                        val nonSequentialDto2 = TemplateFieldCreateReqServiceDto(
                            id = "field2",
                            name = "Field 2",
                            icon = "icon2",
                            type = TestFieldType,
                            order = 3, // 2가 빠져있음
                            isDisplay = false,
                            attributes = setOf(createTestAttributeCreateReqServiceDto())
                        )
                        val nonSequentialDtoList = listOf(nonSequentialDto1, nonSequentialDto2)

                        it("ResourceNotValid 예외를 던진다") {
                            val ex = assertThrows<ResourceNotValid> {
                                templateFieldService.create(nonSequentialDtoList)
                            }
                            ex.name shouldBe "TemplateField"
                            ex.reasons shouldBe listOf("Order values must be a continuous sequence starting from 1 to 2.")
                        }
                    }

                    context("Id가 중복되면") {
                        val duplicateIdDto1 = TemplateFieldCreateReqServiceDto(
                            id = "field1",
                            name = "Field 1",
                            icon = "icon1",
                            type = TestFieldType,
                            order = 1,
                            isDisplay = true,
                            attributes = setOf(createTestAttributeCreateReqServiceDto())
                        )
                        val duplicateIdDto2 = TemplateFieldCreateReqServiceDto(
                            id = "field1", // 중복된 Id
                            name = "Field 2",
                            icon = "icon2",
                            type = TestFieldType,
                            order = 2,
                            isDisplay = false,
                            attributes = setOf(createTestAttributeCreateReqServiceDto())
                        )
                        val duplicateIdDtoList = listOf(duplicateIdDto1, duplicateIdDto2)

                        it("ResourceNotValid 예외를 던진다") {
                            val ex = assertThrows<ResourceNotValid> {
                                templateFieldService.create(duplicateIdDtoList)
                            }
                        }
                    }
                    context("isDisplay 가 허용되지 않는 필드타입인데 isDisplay = true 로 설정하면") {
                        val invalidDisplayDto = TemplateFieldCreateReqServiceDto(
                            id = "field1",
                            name = "Field 1",
                            icon = "icon1",
                            type = TestFieldTypeCantDisplay, // canDisplay = true 인 필드타입 사용
                            order = 1,
                            isDisplay = true, // 허용되지 않는 필드타입인데 isDisplay =
                            attributes = setOf(createTestAttributeCreateReqServiceDto())
                        )
                        it("ResourceNotValid 예외를 던진다") {
                            val ex = assertThrows<ResourceNotValid> {
                                templateFieldService.create(listOf(invalidDisplayDto))
                            }
                        }
                    }
                    //TODO : 나중에 설정값 통해서 최대 표시 개수 제한 걸기
                    context("isDisplay 값이 표시 제한 개수를 초과하면") {
                        it("ResourceNotValid 예외를 던진다") {
                        }
                    }
                }
            }
        }
    }
}