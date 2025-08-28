package com.gabinote.coffeenote.common.mapping.attribute

import com.gabinote.coffeenote.common.domain.attribute.Attribute
import com.gabinote.coffeenote.common.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.common.dto.attribute.controller.AttributeResControllerDto
import com.gabinote.coffeenote.common.dto.attribute.controller.AttributeUpdateReqControllerDto
import com.gabinote.coffeenote.common.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.common.dto.attribute.service.AttributeResServiceDto
import com.gabinote.coffeenote.common.dto.attribute.service.AttributeUpdateReqServiceDto
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import io.kotest.matchers.shouldBe

class AttributeMapperTest : MockkTestTemplate() {

    val attributeMapper: AttributeMapper = AttributeMapperImpl()

    init {
        describe("[Common] AttributeMapper") {

            describe("AttributeMapper.toAttributeResServiceDto") {
                context("Attribute 엔티티가 주어지면,") {
                    val attribute = Attribute(
                        key = "flavor",
                        value = setOf("sweet", "bitter")
                    )
                    val expected = AttributeResServiceDto(
                        key = attribute.key,
                        value = attribute.value
                    )
                    it("AttributeResServiceDto로 변환되어야 한다.") {
                        val result = attributeMapper.toAttributeResServiceDto(attribute)
                        result shouldBe expected
                    }
                }
            }

            describe("AttributeMapper.toAttributeResControllerDto") {
                context("AttributeResServiceDto가 주어지면,") {
                    val dto = AttributeResServiceDto(
                        key = "aroma",
                        value = setOf("fruity", "nutty")
                    )
                    val expected = AttributeResControllerDto(
                        key = dto.key,
                        value = dto.value
                    )
                    it("AttributeResControllerDto로 변환되어야 한다.") {
                        val result = attributeMapper.toAttributeResControllerDto(dto)
                        result shouldBe expected
                    }
                }
            }

            describe("AttributeMapper.toAttribute") {
                context("AttributeCreateReqServiceDto가 주어지면,") {
                    val dto = AttributeCreateReqServiceDto(
                        key = "body",
                        value = setOf("heavy", "light")
                    )
                    val expected = Attribute(
                        key = dto.key,
                        value = dto.value
                    )
                    it("Attribute 엔티티로 변환되어야 한다.") {
                        val result = attributeMapper.toAttribute(dto)
                        result shouldBe expected
                    }
                }
            }

            describe("AttributeMapper.toAttributeCreateReqServiceDto") {
                context("AttributeCreateReqControllerDto가 주어지면,") {
                    val dto = AttributeCreateReqControllerDto(
                        key = "acidity",
                        value = setOf("high", "low")
                    )
                    val expected = AttributeCreateReqServiceDto(
                        key = dto.key,
                        value = dto.value
                    )
                    it("AttributeCreateReqServiceDto로 변환되어야 한다.") {
                        val result = attributeMapper.toAttributeCreateReqServiceDto(dto)
                        result shouldBe expected
                    }
                }
            }

            describe("AttributeMapper.toAttributeUpdateReqServiceDto") {
                context("AttributeUpdateReqControllerDto가 주어지면,") {
                    val dto = AttributeUpdateReqControllerDto(
                        key = "aftertaste",
                        value = setOf("long", "short")
                    )
                    val expected = AttributeUpdateReqServiceDto(
                        key = dto.key,
                        value = dto.value
                    )
                    it("AttributeCreateReqServiceDto로 변환되어야 한다.") {
                        val result = attributeMapper.toAttributeUpdateReqServiceDto(dto)
                        result shouldBe expected
                    }
                }
            }
        }
    }
}