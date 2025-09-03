package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe

class TextFieldTypeTest : MockkTestTemplate() {
    init {
        describe("[Field] TextFieldType Test") {
            describe("validation attributes") {
                context("올바른 attributes가 주어지면") {
                    val validAttributes = setOf<Attribute>()

                    it("Validation에 성공한다.") {
                        val res = TextField.validationAttributes(validAttributes)
                        res.all { it.valid } shouldBe true
                    }
                }

                context("빈 Attribute가 아닌 값이 주어지면") {
                    val invalidAttributes = setOf(Attribute("key", setOf("value")))

                    it("Validation에 실패한다.") {
                        val res = TextField.validationAttributes(invalidAttributes)
                        res.all { !it.valid } shouldBe true
                    }
                }
            }

            describe("validation values") {
                context("올바른 Values 가 주어지면") {
                    val validValue = "this is text field value"

                    it("Validation에 성공한다.") {
                        val res = TextField.validationValues(values = setOf(validValue), attributes = setOf())
                        res.all { it.valid } shouldBe true
                    }
                }

                describe("올바르지 않은 Values가 주어지면") {
                    table(
                        headers("invalidValue", "reason"),
                        row(
                            setOf("this is text field value", "this is text field value2"),
                            "values가 1개가 아닌 경우"
                        ),
                        row(
                            setOf("a".repeat(101)),
                            "100자를 초과하는 경우"
                        )
                    ).forAll { invalidValue, reason ->
                        context(reason) {
                            it("Validation에 실패한다.") {
                                val res = TextField.validationValues(values = invalidValue, attributes = setOf())
                                res.all { !it.valid } shouldBe true
                            }
                        }
                    }
                }
            }
        }
    }
}