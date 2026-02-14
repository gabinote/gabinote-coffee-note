package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.field.domain.fieldType.type.ImageFieldType
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import io.kotest.matchers.shouldBe
import java.util.*

class ImageFieldTypeTest : MockkTestTemplate() {

    private val imageFieldType = ImageFieldType()

    init {
        describe("[Field] ImageFieldType Test") {
            describe("validation attributes") {
                context("빈 Attributes가 주어지면") {
                    val validAttributes = emptySet<com.gabinote.coffeenote.field.domain.attribute.Attribute>()
                    it("Validation 에 통과한다.") {
                        val res = imageFieldType.validationAttributes(validAttributes)
                        res.all { it.valid } shouldBe true
                    }
                }

                context("비어있지 않은 Attributes가 주어지면") {
                    val invalidAttributes = setOf(
                        com.gabinote.coffeenote.field.domain.attribute.Attribute("key", setOf("value"))
                    )
                    it("Validation 에 통과하지 못한다.") {
                        val res = imageFieldType.validationAttributes(invalidAttributes)
                        res.all { !it.valid } shouldBe true
                    }
                }
            }

            describe("validation values") {
                context("올바른 Image Value가 주어지면") {
                    val validValues = setOf(UUID.randomUUID().toString())
                    it("Validation 에 통과한다.") {
                        val res = imageFieldType.validationValues(validValues, emptySet())

                        res.all { it.valid } shouldBe true
                    }
                }

                context("Value가 한개가 아니라면") {
                    val invalidValues = setOf(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                    it("Validation 에 통과하지 못한다.") {
                        val res = imageFieldType.validationValues(invalidValues, emptySet())
                        res.all { !it.valid } shouldBe true
                    }
                }
            }
        }
    }

}