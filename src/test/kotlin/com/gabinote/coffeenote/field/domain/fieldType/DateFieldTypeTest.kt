package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import io.kotest.matchers.shouldBe


//TODO: 뒤에 작성한 테스트에 맞추어 테스트 형식 변경하기
class DateFieldTypeTest : MockkTestTemplate() {
    init {
        describe("[Field] DateFieldType Test") {
            describe("validation attributes") {

                context("빈 Attributes가 주어지면") {
                    val validAttributes = setOf<Attribute>()
                    it("Validation 에 통과한다.") {
                        val res = DateField.validationAttributes(validAttributes)
                        res.all { it.valid } shouldBe true
                    }
                }

                context("비어있지 않은 Attributes가 주어지면") {
                    val invalidAttributes = setOf(
                        Attribute("key", setOf("value"))
                    )
                    it("Validation 에 통과하지 못한다.") {
                        val res = DateField.validationAttributes(invalidAttributes)
                        res.all { !it.valid } shouldBe true
                    }
                }
            }

            describe("validation values") {
                context("올바른 Date Value가 주어지면") {
                    val validValues = setOf("2020-01-01")
                    it("Validation 에 통과한다.") {
                        val res = DateField.validationValues(validValues, setOf())

                        res.all { it.valid } shouldBe true
                    }
                }

                context("올바르지 않은 Date Value가 주어지면") {
                    val invalidValues = setOf("invalid-date")
                    it("Validation 에 통과하지 못한다.") {
                        val res = DateField.validationValues(invalidValues, setOf())
                        res.all { !it.valid } shouldBe true
                    }
                }

                context("ISO-8601 형식이 아닌 Date Value가 주어지면") {
                    val invalidValues = setOf("01-01-2020")
                    it("Validation 에 통과하지 못한다.") {
                        val res = DateField.validationValues(invalidValues, setOf())
                        res.all { !it.valid } shouldBe true
                    }
                }

                context("Value가 한개가 아니라면") {
                    val invalidValues = setOf("2020-01-01", "2020-01-02")
                    it("Validation 에 통과하지 못한다.") {
                        val res = DateField.validationValues(invalidValues, setOf())
                        res.all { !it.valid } shouldBe true
                    }
                }

                context("Value가 빈 값이라면") {
                    val invalidValues = setOf<String>()
                    it("Validation 에 통과하지 못한다.") {
                        val res = DateField.validationValues(invalidValues, setOf())
                        res.all { !it.valid } shouldBe true
                    }
                }
            }
        }
    }
}