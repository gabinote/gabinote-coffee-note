package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe

class LongTextFieldTypeTest : MockkTestTemplate() {
    init {
        describe("[Field] LongTextFieldType Test") {
            describe("validation attributes") {
                context("올바른 Attribute가 주어진 경우") {
                    val validAttributes = setOf<Attribute>()
                    it("Validation 에 성공한다") {
                        val res = LongTextField.validationAttributes(validAttributes)
                        res.all { it.valid } shouldBe true
                    }
                }

                context("빈 Attributes가 아닌 경우") {
                    val invalidAttribute = setOf(
                        Attribute(
                            key = "invalid",
                            value = setOf("long text not have attributes")
                        )
                    )

                    it("Validation에 실패한다.") {
                        val res = LongTextField.validationAttributes(invalidAttribute)
                        res.all { !it.valid } shouldBe true
                    }
                }
            }

            describe("validation values") {
                context("올바른 value가 주어진 경우") {
                    val validValue = "this is long text field value"
                    it("Validation 에 성공한다") {
                        val res = LongTextField.validationValues(values = setOf(validValue), attributes = setOf())
                        res.all { it.valid } shouldBe true
                    }
                }

                describe("올바르지 않은 value가 주어지면") {
                    table(
                        headers("invalidValue", "reason"),
                        row(setOf("a".repeat(10001)), "10000 자를 초과하는 경우"),
                        row(setOf("1", "2"), "values가 1개가 아닌 경우"),
                        row(setOf<String>(), "빈 값인 경우")
                    ).forAll { invalidValue, reason ->
                        context(reason) {
                            it("Validation 에 실패한다") {
                                val res = LongTextField.validationValues(values = invalidValue, attributes = setOf())
                                res.all { !it.valid } shouldBe true
                            }
                        }
                    }
                }


            }
        }
    }
}