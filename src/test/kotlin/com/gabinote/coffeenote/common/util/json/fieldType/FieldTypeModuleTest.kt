package com.gabinote.coffeenote.common.util.json.fieldType

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeFactory
import com.gabinote.coffeenote.testSupport.testTemplate.JsonTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldType
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.json.JacksonTester


class FieldTypeModuleTest : JsonTestTemplate() {
    @Autowired
    private lateinit var jacksonTester: JacksonTester<FieldType>

    @MockkBean
    private lateinit var fieldTypeFactory: FieldTypeFactory


    init {
        beforeTest {
            clearMocks(fieldTypeFactory)
        }

        describe("[Common] FieldTypeModule Test") {
            describe("역직렬화 테스트") {
                context("올바른 FieldTypeKey가 주어지면") {
                    val validKey = "TEST"
                    val validType = TestFieldType
                    beforeTest {
                        every { fieldTypeFactory.getFieldType(validKey) } returns validType
                    }
                    val validJson = "\"$validKey\""
                    it("FieldTypeKey에 해당하는 AbstractFieldType 객체로 역직렬화된다.") {
                        val res = jacksonTester.parseObject(validJson)
                        res shouldBe validType
                    }
                }

                context("잘못된 FieldTypeKey가 주어지면") {
                    val invalidKey = "INVALID_KEY"
                    beforeTest {
                        every { fieldTypeFactory.getFieldType(invalidKey) } returns null
                    }
                    val invalidJson = "\"$invalidKey\""
                    it("MismatchedInputException 예외가 발생한다.") {
                        assertThrows<MismatchedInputException> {
                            jacksonTester.parseObject(invalidJson)
                        }
                    }
                }

                context("빈 문자가 주어지면") {
                    val emptyJson = "\"\""
                    beforeTest {
                        every { fieldTypeFactory.getFieldType("") } returns null
                    }
                    it("MismatchedInputException 예외가 발생한다.") {
                        assertThrows<MismatchedInputException> {
                            jacksonTester.parseObject(emptyJson)
                        }
                    }
                }
            }
            describe("직렬화 테스트") {
                context("FieldType 객체가 주어지면") {
                    val fieldType = TestFieldType
                    val expectedJson = "\"${fieldType.getKeyString()}\""
                    it("FieldTypeKey에 해당하는 문자열로 직렬화된다.") {
                        val res = jacksonTester.write(fieldType)
                        res.json shouldBe expectedJson
                    }
                }
            }
        }
    }
}