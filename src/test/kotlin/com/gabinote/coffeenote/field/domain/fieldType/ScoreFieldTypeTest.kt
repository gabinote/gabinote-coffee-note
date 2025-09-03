package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe


class ScoreFieldTypeTest : MockkTestTemplate() {
    init {
        describe("[Field] ScoreFieldType Test") {
            describe("validation attributes") {
                context("올바른 attributes가 주어지면") {
                    val validAttributes = setOf(
                        Attribute(key = "maxScore", value = setOf("10")),
                    )

                    it("Validation 에 성공한다") {
                        val res = ScoreField.validationAttributes(validAttributes)
                        res.all { it.valid } shouldBe true
                    }
                }

                describe("올바르지 않은 attributes가 주어지면") {
                    table(
                        headers("invalidAttributes", "reason"),
                        row(
                            setOf(Attribute(key = "maxScore", value = setOf("a"))),
                            "숫자가 아닌 maxScore 가 주어진 경우"
                        ),
                        row(
                            setOf(Attribute(key = "maxScore", value = setOf("-1"))),
                            "3 이하의 maxScore 가 주어진 경우"
                        ),
                        row(
                            setOf(Attribute(key = "maxScore", value = setOf("1000000000"))),
                            "10을 초과하는 maxScore가 주어진 경우"
                        ),
                        row(
                            setOf(Attribute(key = "invalid", value = setOf("10"))),
                            "maxScore가 아닌 다른 key가 주어진 경우"
                        )
                    ).forAll { invalidAttributes, reason ->
                        context(reason) {
                            it("Validation 에 실패한다") {
                                val res = ScoreField.validationAttributes(invalidAttributes)
                                res.all { !it.valid } shouldBe true
                            }
                        }
                    }
                }

            }

            describe("validation values") {

                context("올바른 Values가 주어진 경우") {
                    val validValues = setOf("10")
                    val attributes = setOf(Attribute(key = "maxScore", value = setOf("10")))

                    it("Validation에 성공한다.") {
                        val res = ScoreField.validationValues(validValues, attributes)
                        res.all { it.valid } shouldBe true
                    }
                }

                describe("올바르지 않은 Values가 주어지면") {
                    table(
                        headers("invalidValue", "attributes", "reason"),
                        row(
                            setOf("11"),
                            setOf(Attribute(key = "maxScore", value = setOf("10"))),
                            "maxScore를 초과하는 경우"
                        ),
                        row(
                            setOf("-1"),
                            setOf(Attribute(key = "maxScore", value = setOf("10"))),
                            "음수인 경우"
                        ),
                        row(
                            setOf("a"),
                            setOf(Attribute(key = "maxScore", value = setOf("10"))),
                            "숫자가 아닌 경우"
                        ),
                        row(
                            setOf("1", "2"),
                            setOf(Attribute(key = "maxScore", value = setOf("10"))),
                            "values가 1개가 아닌 경우"
                        ),
                        row(
                            setOf(),
                            setOf(Attribute(key = "maxScore", value = setOf("10"))),
                            "빈 값인 경우"
                        )
                    ).forAll { invalidValue, attributes, reason ->
                        context(reason) {
                            it("Validation 에 실패한다") {
                                val res = ScoreField.validationValues(invalidValue, attributes)
                                res.all { !it.valid } shouldBe true
                            }
                        }
                    }
                }

            }
        }
    }
}