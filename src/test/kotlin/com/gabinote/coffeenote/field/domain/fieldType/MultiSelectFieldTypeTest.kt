package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.TestCollectionHelper
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe

class MultiSelectFieldTypeTest : MockkTestTemplate() {
    init {
        describe("[Field] MultiSelectFieldType Test") {
            describe("validation attributes") {
                context("올바른 Attribute가 주어지면") {
                    val validAttributes = setOf(
                        Attribute(key = "values", value = setOf("a", "b", "c")),
                        Attribute(key = "allowAddValue", value = setOf("true"))
                    )

                    it("Validation 에 통과한다.") {
                        val res = MultiSelectField.validationAttributes(validAttributes)
                        res.all { it.valid } shouldBe true
                    }
                }

                describe("Key 검증") {

                    table(
                        headers("invalidAttributes", "reason"),
                        row(
                            setOf<Attribute>(
                                Attribute(key = "allowAddValue", value = setOf("true"))
                            ), "values attribute 가 누락되있으면"
                        ),
                        row(
                            setOf<Attribute>(
                                Attribute(key = "values", value = setOf("a", "b", "c"))
                            ), "allowAddValue attribute 가 누락되있으면"
                        ),
                        row(
                            setOf<Attribute>(),
                            "모든 attribute가 누락되어있으면"
                        )
                    ).forAll { invalidAttributes, reason ->
                        context(reason) {
                            it("Validation 에 통과하지 못한다.") {
                                val res = MultiSelectField.validationAttributes(invalidAttributes)
                                res.any { !it.valid } shouldBe true
                            }
                        }
                    }
                }

                describe("values value 검증") {

                    table(
                        headers("invalidAttributes", "reason"),
                        row(
                            setOf(
                                Attribute(key = "values", value = setOf("a")),
                                Attribute(key = "allowAddValue", value = setOf("true"))
                            ),
                            "values attributes 의 value 가 2개 미만이면"
                        ),
                        row(
                            setOf(
                                Attribute(key = "values", value = setOf()),
                                Attribute(key = "allowAddValue", value = setOf("true"))
                            ),
                            "values attributes 의 value 가 빈 값이면"
                        ),
                        row(
                            setOf(
                                Attribute(key = "values", value = setOf("a".repeat(51), "b")),
                                Attribute(key = "allowAddValue", value = setOf("true"))
                            ),
                            "values attributes 의 value 중 하나가 50자를 초과하면"
                        ),
                        row(
                            setOf(
                                Attribute(key = "values", value = setOf("", "b")),
                                Attribute(key = "allowAddValue", value = setOf("true"))
                            ),
                            "values attributes 의 value 중 하나가 공백이면"
                        ),
                        row(
                            setOf(
                                Attribute(key = "values", value = setOf("a", "a")),
                                Attribute(key = "allowAddValue", value = setOf("true"))
                            ),
                            "values attributes 의 value 중 중복된 값이 있으면"
                        ),
                        row(
                            setOf(
                                Attribute(
                                    key = "values",
                                    value = TestCollectionHelper.generateRandomStringSet(maxLength = 50, count = 101)
                                ),
                                Attribute(key = "allowAddValue", value = setOf("true"))
                            ),
                            "values attributes 의 value 가 100개를 초과하면"
                        ),
                    ).forAll { invalidAttributes, reason ->
                        context(reason) {
                            it("Validation 에 통과하지 못한다.") {
                                val res = MultiSelectField.validationAttributes(invalidAttributes)
                                res.any { !it.valid } shouldBe true
                            }
                        }
                    }

                }

                describe("allowAddValue value 검증") {
                    table(
                        headers("invalidAttributes", "reason"),
                        row(
                            setOf(
                                Attribute(key = "values", value = setOf("a", "b")),
                                Attribute(key = "allowAddValue", value = setOf("true", "false"))
                            ),
                            "allowAddValue attributes 의 value 가 1개가 아니면"
                        ),
                        row(
                            setOf(
                                Attribute(key = "values", value = setOf("a", "b")),
                                Attribute(key = "allowAddValue", value = setOf("끼얏호우"))
                            ),
                            "allowAddValue attributes 의 value 가 true/false 가 아니면"
                        ),
                        row(
                            setOf(
                                Attribute(key = "values", value = setOf("a", "b")),
                                Attribute(key = "allowAddValue", value = setOf())
                            ),
                            "allowAddValue attributes 의 value 가 빈 값이면"
                        ),
                    ).forAll { invalidAttributes, reason ->
                        context(reason) {
                            it("Validation 에 통과하지 못한다.") {
                                val res = MultiSelectField.validationAttributes(invalidAttributes)
                                res.any { !it.valid } shouldBe true
                            }
                        }
                    }
                }

            }

            describe("validation values") {
                describe("올바른 Values가 주어지면") {
                    table(
                        headers("validValues", "attribute", "reason"),
                        row(
                            setOf("a"),
                            setOf(
                                Attribute(key = "values", value = setOf("a", "b", "c")),
                                Attribute(key = "allowAddValue", value = setOf("true"))
                            ),
                            "하나의 값만 주어진 경우"
                        ),
                        row(
                            setOf("a", "b", "c"),
                            setOf(
                                Attribute(key = "values", value = setOf("a", "b", "c")),
                                Attribute(key = "allowAddValue", value = setOf("true"))
                            ),
                            "여러개의 값이 주어진 경우"
                        ),
                        row(
                            setOf(),
                            setOf(
                                Attribute(key = "values", value = setOf("a", "b", "c")),
                                Attribute(key = "allowAddValue", value = setOf("true"))
                            ),
                            "빈 값인 경우"
                        ),
                        row(
                            setOf("d"),
                            setOf(
                                Attribute(key = "values", value = setOf("a", "b", "c")),
                                Attribute(key = "allowAddValue", value = setOf("true"))
                            ),
                            "allowAddValue 가 true 일때 선택항목에 존재하지 않는 값이 주어지면"
                        )
                    ).forAll { validValues, attributes, reason ->
                        context(reason) {
                            it("Validation 에 통과한다.") {
                                val res = MultiSelectField.validationValues(validValues, attributes)
                                res.all { it.valid } shouldBe true
                            }
                        }
                    }
                }

                describe("올바르지 않은 Values가 주어지면") {
                    val randomSet = TestCollectionHelper.generateRandomStringSet(maxLength = 50, count = 31)
                    table(
                        headers("invalidValues", "attribute", "reason"),
                        row(
                            setOf("d"),
                            setOf(
                                Attribute(key = "values", value = setOf("a", "b", "c")),
                                Attribute(key = "allowAddValue", value = setOf("false"))
                            ),
                            "allowAddValue 가 false 일때 선택항목에 존재하지 않는 값이 주어지면"
                        ),
                        row(
                            setOf("a".repeat(51)),
                            setOf(
                                Attribute(key = "values", value = setOf("a", "b", "c")),
                                Attribute(key = "allowAddValue", value = setOf("true"))
                            ),
                            "값 중 하나가 50자를 초과하면"
                        ),
                        row(
                            setOf("", "b", "d"),
                            setOf(
                                Attribute(key = "values", value = setOf("a", "b", "c")),
                                Attribute(key = "allowAddValue", value = setOf("true"))
                            ),
                            "값 중 하나가 빈 값이면"
                        ),
                        row(
                            randomSet,
                            setOf(
                                Attribute(key = "values", value = randomSet),
                                Attribute(key = "allowAddValue", value = setOf("true"))
                            ),
                            "값의 개수가 30개를 초과하면"
                        ),
                    ).forAll { invalidValues, attributes, reason ->
                        context(reason) {
                            it("Validation 에 통과하지 못한다.") {
                                val res = MultiSelectField.validationValues(invalidValues, attributes)
                                res.any { !it.valid } shouldBe true
                            }
                        }
                    }

                }
            }
        }
    }
}