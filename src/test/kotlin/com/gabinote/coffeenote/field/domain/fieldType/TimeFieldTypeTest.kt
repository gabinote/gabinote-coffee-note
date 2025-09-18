package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.fieldType.type.TimeFieldType
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe

class TimeFieldTypeTest : MockkTestTemplate() {

    private val timeFieldType = TimeFieldType()

    init {
        describe("[Field] TimeFieldType Test") {
            describe("validation attributes") {

                describe("올바른 Attribute가 주어진 경우") {
                    table(
                        headers("validAttributes", "reason"),
                        row(setOf(Attribute(key = "24Format", value = setOf("true"))), "24Format이 true인 경우"),
                        row(setOf(Attribute(key = "24Format", value = setOf("false"))), "24Format이 false인 경우"),
                    ).forAll { validAttributes, reason ->
                        context(reason) {
                            it("Validation 에 성공한다") {
                                val res = timeFieldType.validationAttributes(validAttributes)
                                res.all { it.valid } shouldBe true
                            }
                        }
                    }
                }

                describe("올바르지 않은 Attribute가 주어진 경우") {
                    table(
                        headers("invalidAttributes", "reason"),
                        row(setOf(Attribute(key = "24Format", value = setOf("a"))), "24Format이 true/false가 아닌 경우"),
                        row(setOf(Attribute(key = "24Format", value = setOf("true", "false"))), "24Format이 2개 이상인 경우"),
                        row(setOf(Attribute(key = "invalid", value = setOf("true"))), "정의되지 않은 key가 포함된 경우"),
                    ).forAll { invalidAttributes, reason ->
                        context(reason) {
                            it("Validation 에 실패한다") {
                                val res = timeFieldType.validationAttributes(invalidAttributes)
                                res.all { !it.valid } shouldBe true
                            }
                        }
                    }
                }
            }

            describe("validation values") {

                describe("올바른 Values가 주어진 경우") {
                    table(
                        headers("validValues", "reason"),
                        row(setOf("01:00"), "01:00이 주어진 경우"),
                        row(setOf("23:59"), "23:59이 주어진 경우"),
                    ).forAll { validValues, reason ->
                        context(reason) {
                            it("Validation 에 성공한다") {
                                val res = timeFieldType.validationValues(values = validValues, attributes = setOf())
                                res.all { it.valid } shouldBe true
                            }
                        }
                    }
                }

                describe("올바르지 않은 Values가 주어진 경우") {
                    table(
                        headers("invalidValues", "reason"),
                        row(setOf("00:60"), "00:60이 주어진 경우"),
                        row(setOf("1:00"), "1:00이 주어진 경우"),
                        row(setOf("01:0"), "01:0이 주어진 경우"),
                        row(setOf("0100"), "0100이 주어진 경우"),
                        row(setOf("invalid"), "시간 형식이 아닌 값이 주어진 경우"),
                        row(setOf("01:00", "02:00"), "2개 이상의 값이 주어진 경우"),
                        row(emptySet<String>(), "빈 값이 주어진 경우"),
                    ).forAll { invalidValues, reason ->
                        context(reason) {
                            it("Validation 에 실패한다") {
                                val res = timeFieldType.validationValues(values = invalidValues, attributes = setOf())
                                res.all { !it.valid } shouldBe true
                            }
                        }
                    }
                }

            }
        }
    }
}