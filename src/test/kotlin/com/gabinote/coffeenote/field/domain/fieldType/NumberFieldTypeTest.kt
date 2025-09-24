package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.fieldType.type.NumberFieldType
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe

class NumberFieldTypeTest : MockkTestTemplate() {

    private val numberFieldType = NumberFieldType()

    init {
        describe("[Field] NumberFieldType Test") {
            describe("validation attributes") {
                context("올바른 Attribute가 주어진 경우") {
                    val validAttributes = setOf(
                        Attribute(key = "unit", value = setOf("kg")),
                    )
                    it("Validation 에 성공한다") {
                        val res = numberFieldType.validationAttributes(validAttributes)
                        res.all { it.valid } shouldBe true
                    }
                }

                describe("올바르지 않은 Attribute가 주어진 경우") {
                    table(
                        headers("invalidAttribute", "reason"),
                        row(
                            setOf(Attribute(key = "unit", value = setOf("a", "b"))),
                            "unit이 1개가 아닌 경우"
                        ),
                        row(
                            setOf(
                                Attribute(
                                    key = "unit",
                                    value = setOf("")
                                )
                            ),
                            "unit의 value가 빈값이면"
                        ),
                        row(
                            setOf(
                                Attribute(
                                    key = "unit",
                                    value = setOf(" ")
                                )
                            ),
                            "unit의 value가 공백이면"
                        ),
                        row(
                            setOf(
                                Attribute(
                                    key = "not valid Key",
                                    value = setOf(" ")
                                )
                            ),
                            "정의되지 않은 key가 포함된 경우"
                        )
                    ).forAll { invalidAttribute, reason ->
                        context(reason) {
                            it("Validation 에 실패한다") {
                                val res = numberFieldType.validationAttributes(invalidAttribute)
                                res.all { !it.valid } shouldBe true
                            }
                        }
                    }
                }
            }

            describe("validation values") {
                context("올바른 value가 주어진 경우") {
                    val validValue = "50"
                    it("Validation 에 성공한다") {
                        val res = numberFieldType.validationValues(
                            values = setOf(validValue), attributes = setOf(
                                Attribute(
                                    key = "unit",
                                    value = setOf("kg")
                                )
                            )
                        )
                        res.all { it.valid } shouldBe true
                    }
                }

                describe("올바르지 않은 value가 주어진 경우") {
                    table(
                        headers("invalidValue", "reason"),
                        row(setOf("a".repeat(51)), "50 자를 초과하는 경우"),
                        row(setOf("1", "2"), "values가 1개가 아닌 경우"),
                        row(setOf<String>(), "빈 값인 경우")
                    ).forAll { invalidValue, reason ->
                        context(reason) {
                            it("Validation 에 실패한다") {
                                val res = numberFieldType.validationValues(values = invalidValue, attributes = setOf())
                                res.all { !it.valid } shouldBe true
                            }
                        }
                    }
                }

            }
        }
    }
}