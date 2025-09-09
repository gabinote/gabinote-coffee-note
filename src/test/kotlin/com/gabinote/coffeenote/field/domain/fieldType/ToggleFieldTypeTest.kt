package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe


class ToggleFieldTypeTest : MockkTestTemplate() {
    init {
        describe("[Field] ToggleFieldType Test") {
            describe("validation attributes") {

                context("올바른 Attribute가 주어진 경우") {

                    val validAttributes = emptySet<Attribute>()
                    it("Validation 에 성공한다") {
                        val res = ToggleField.validationAttributes(validAttributes)
                        res.all { it.valid } shouldBe true
                    }
                }

                context("올바르지 않은 Attribute가 주어진 경우") {
                    val invalidAttributes = setOf(
                        Attribute(key = "invalid", value = setOf("value"))
                    )
                    it("Validation 에 실패한다") {
                        val res = ToggleField.validationAttributes(invalidAttributes)
                        res.all { !it.valid } shouldBe true
                    }
                }

            }

            describe("validation values") {
                describe("올바른 Values가 주어진 경우") {
                    table(
                        headers("validValues", "reason"),
                        row(setOf("true"), "true가 주어진 경우"),
                        row(setOf("false"), "false가 주어진 경우"),
                    ).forAll { validValues, reason ->
                        context(reason) {
                            it("Validation 에 성공한다") {
                                val res = ToggleField.validationValues(values = validValues, attributes = setOf())
                                res.all { it.valid } shouldBe true
                            }
                        }
                    }
                }

                describe("올바르지 않은 Values가 주어진 경우") {
                    table(
                        headers("invalidValues", "reason"),
                        row(setOf("true", "false"), "2개 이상의 값이 주어진 경우"),
                        row(setOf("invalid"), "true, false 외의 값이 주어진 경우"),
                        row(emptySet<String>(), "빈 값이 주어진 경우"),
                    ).forAll { invalidValues, reason ->
                        context(reason) {
                            it("Validation 에 실패한다") {
                                val res = ToggleField.validationValues(values = invalidValues, attributes = setOf())
                                res.all { !it.valid } shouldBe true
                            }
                        }
                    }
                }

            }
        }
    }
}