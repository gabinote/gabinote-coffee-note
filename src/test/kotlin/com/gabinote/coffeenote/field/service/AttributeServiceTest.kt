package com.gabinote.coffeenote.field.service

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotValid
import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeUpdateReqServiceDto
import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapper
import com.gabinote.coffeenote.field.service.attribute.AttributeService
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldType
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows


class AttributeServiceTest : ServiceTestTemplate() {

    private lateinit var attributeService: AttributeService

    @MockK
    private lateinit var attributeMapper: AttributeMapper

    init {
        beforeTest {
            clearAllMocks()
            attributeService = AttributeService(attributeMapper)
        }

        describe("[Field] AttributeService Test") {

            describe("AttributeService.createAttribute") {
                context("올바른 값이 주어지면") {
                    val testType = TestFieldType
                    val attributesCreateReq = mockk<AttributeCreateReqServiceDto>()

                    val attribute = Attribute(
                        key = "isValid",
                        value = setOf("true")
                    )

                    beforeTest {
                        every { attributeMapper.toAttribute(dto = attributesCreateReq) } returns attribute
                    }

                    it("속성을 생성한다") {
                        val result = attributeService.createAttribute(
                            fieldType = testType,
                            attributesCreateReq = setOf(attributesCreateReq)
                        )

                        result.size shouldBe 1
                        result.first().key shouldBe attribute.key
                        result.first().value shouldBe attribute.value

                        verify(exactly = 1) { attributeMapper.toAttribute(dto = attributesCreateReq) }
                    }
                }

                context("올바르지 않은 Attribute 값이 주어지면") {
                    val testType = TestFieldType
                    val attributesCreateReq = mockk<AttributeCreateReqServiceDto>()
                    val attribute = Attribute(
                        key = "isValid",
                        value = setOf("false")
                    )
                    beforeTest {
                        every { attributeMapper.toAttribute(dto = attributesCreateReq) } returns attribute
                    }
                    it("ResourceNotValid 예외를 던진다") {
                        assertThrows<ResourceNotValid> {
                            attributeService.createAttribute(
                                fieldType = testType,
                                attributesCreateReq = setOf(attributesCreateReq)
                            )
                        }

                        verify(exactly = 1) { attributeMapper.toAttribute(dto = attributesCreateReq) }

                    }
                }

            }

            describe("AttributeService.updateAttribute") {
                context("변경될 속성이 비어져 있으면") {

                    val old = setOf(
                        Attribute(
                            key = "isValid",
                            value = setOf("true")
                        )
                    )

                    val newSet = emptySet<AttributeUpdateReqServiceDto>()
                    it("기존 속성을 반환한다") {

                        val result = attributeService.updateAttribute(
                            fieldType = TestFieldType,
                            oldAttributes = old,
                            newAttributeReq = newSet,
                        )

                        result shouldBe old

                    }
                }
                context("변경될 속성이 기존 속성과 동일하면") {

                    val old = setOf(
                        Attribute(
                            key = "isValid",
                            value = setOf("true")
                        )
                    )

                    val newSet = setOf(
                        AttributeUpdateReqServiceDto(
                            key = "isValid",
                            value = setOf("true")
                        )
                    )

                    val newAttribute = Attribute(
                        key = "isValid",
                        value = setOf("true")
                    )

                    beforeTest {
                        every { attributeMapper.toAttribute(dto = newSet.first()) } returns newAttribute
                    }

                    it("기존 속성을 반환한다") {
                        val result = attributeService.updateAttribute(
                            fieldType = TestFieldType,
                            oldAttributes = old,
                            newAttributeReq = newSet
                        )

                        result shouldBe old

                        verify(exactly = 1) { attributeMapper.toAttribute(dto = newSet.first()) }
                    }
                }

                context("올바른 값이 주어지면") {
                    val old = setOf(
                        Attribute(
                            key = "isValid",
                            value = setOf("true")
                        )
                    )

                    val newSet = setOf(
                        AttributeUpdateReqServiceDto(
                            key = "isValid",
                            value = setOf("ok")
                        )
                    )

                    val newAttribute = Attribute(
                        key = "isValid",
                        value = setOf("ok")
                    )

                    beforeTest {
                        every { attributeMapper.toAttribute(dto = newSet.first()) } returns newAttribute
                    }

                    it("기존 속성을 수정하고, 수정된 속성을 반환한다") {
                        val result = attributeService.updateAttribute(
                            fieldType = TestFieldType,
                            oldAttributes = old,
                            newAttributeReq = newSet
                        )

                        result.size shouldBe 1
                        result.first().key shouldBe "isValid"
                        result.first().value shouldBe setOf("ok")

                        verify(exactly = 1) { attributeMapper.toAttribute(dto = newSet.first()) }

                    }


                }

                context("올바르지 않은 Attribute 값이 주어지면") {

                    val old = setOf(
                        Attribute(
                            key = "isValid",
                            value = setOf("true")
                        )
                    )

                    val newSet = setOf(
                        AttributeUpdateReqServiceDto(
                            key = "isValid",
                            value = setOf("false")
                        )
                    )

                    val newAttribute = Attribute(
                        key = "isValid",
                        value = setOf("false")
                    )

                    beforeTest {
                        every { attributeMapper.toAttribute(dto = newSet.first()) } returns newAttribute
                    }

                    it("ResourceNotValid 예외를 던진다") {
                        assertThrows<ResourceNotValid> {
                            attributeService.updateAttribute(
                                fieldType = TestFieldType,
                                oldAttributes = old,
                                newAttributeReq = newSet
                            )
                        }

                        verify(exactly = 1) { attributeMapper.toAttribute(dto = newSet.first()) }

                    }
                }

                context("올바르지 않은 Attribute Key 가 주어지면") {
                    val old = setOf(
                        Attribute(
                            key = "isValid",
                            value = setOf("true")
                        )
                    )
                    val newSet = setOf(
                        AttributeUpdateReqServiceDto(
                            key = "notExist",
                            value = setOf("true")
                        )
                    )

                    val newAttribute = Attribute(
                        key = "notExist",
                        value = setOf("true")
                    )

                    beforeTest {
                        every { attributeMapper.toAttribute(dto = newSet.first()) } returns newAttribute
                    }

                    it("ResourceNotValid 예외를 던진다") {
                        assertThrows<ResourceNotValid> {
                            attributeService.updateAttribute(
                                fieldType = TestFieldType,
                                oldAttributes = old,
                                newAttributeReq = newSet
                            )
                        }

                        verify(exactly = 1) { attributeMapper.toAttribute(dto = newSet.first()) }
                    }
                }

            }
        }
    }

}