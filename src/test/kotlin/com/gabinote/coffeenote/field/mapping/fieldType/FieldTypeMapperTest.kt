package com.gabinote.coffeenote.field.mapping.fieldType

import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeFactory
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldType
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [FieldTypeMapperImpl::class, FieldTypeFactory::class])
class FieldTypeMapperTest : MockkTestTemplate() {

    @Autowired
    lateinit var fieldTypeMapper: FieldTypeMapper

    @MockkBean
    lateinit var fieldTypeFactory: FieldTypeFactory

    init {
        describe("[Field] FieldTypeMapper Test") {
            describe("fieldTypeMapper.toString") {
                context("FieldType 객체가 주어지면") {
                    val fieldType = TestFieldType
                    it("FieldType의 key 문자열을 반환한다") {
                        val result = fieldTypeMapper.toString(fieldType)
                        result shouldBe fieldType.getKeyString()
                    }
                }
            }
            describe("fieldTypeMapper.toFieldType") {
                context("유효한 key 문자열이 주어지면") {
                    val fieldType = TestFieldType
                    beforeEach {
                        every { fieldTypeFactory.getFieldType(fieldType.getKeyString()) } returns fieldType
                    }
                    it("해당 key에 매핑되는 FieldType 객체를 반환한다") {
                        val result = fieldTypeMapper.toFieldType(fieldType.getKeyString())
                        result shouldBe fieldType
                    }
                }
                context("유효하지 않은 key 문자열이 주어지면") {
                    val invalidKey = "invalid_key"
                    beforeEach {
                        every { fieldTypeFactory.getFieldType(invalidKey) } returns null
                    }
                    it("IllegalArgumentException을 던진다") {
                        val exception = kotlin.runCatching { fieldTypeMapper.toFieldType(invalidKey) }.exceptionOrNull()
                        exception shouldBe IllegalArgumentException("Invalid FieldType key: $invalidKey")
                    }
                }

            }
        }
    }
}