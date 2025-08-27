package com.gabinote.coffeenote.common.mapping.attribute

import com.gabinote.coffeenote.common.dto.attribute.controller.AttributeControllerDto
import com.gabinote.coffeenote.common.dto.attribute.service.AttributeServiceDto
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import io.kotest.matchers.shouldBe

class AttributeMapperTest : MockkTestTemplate() {

    val attributeMapper: AttributeMapper = AttributeMapperImpl()

    init {
        describe("[Common] AttributeMapper") {

            describe("AttributeMapper.toAttributeServiceDto") {
                context("AttributeControllerDto가 주어지면,") {
                    val dto = AttributeControllerDto(
                        key = "flavor",
                        value = setOf("sweet", "bitter")
                    )
                    val expected = AttributeServiceDto(
                        key = dto.key,
                        value = dto.value
                    )
                    it("AttributeServiceDto로 변환되어야 한다.") {
                        val result = attributeMapper.toAttributeServiceDto(dto)
                        result shouldBe expected
                    }
                }
            }

            describe("AttributeMapper.toAttributeControllerDto") {
                context("AttributeServiceDto가 주어지면,") {
                    val dto = AttributeServiceDto(
                        key = "aroma",
                        value = setOf("fruity", "nutty")
                    )
                    val expected = AttributeControllerDto(
                        key = dto.key,
                        value = dto.value
                    )
                    it("AttributeControllerDto로 변환되어야 한다.") {
                        val result = attributeMapper.toAttributeControllerDto(dto)
                        result shouldBe expected
                    }
                }
            }
        }
    }
}