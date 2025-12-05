package com.gabinote.coffeenote.note.service.noteFieldService

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotValid
import com.gabinote.coffeenote.field.service.attribute.AttributeService
import com.gabinote.coffeenote.note.domain.note.NoteField
import com.gabinote.coffeenote.note.mapping.noteField.NoteFieldMapper
import com.gabinote.coffeenote.note.service.noteField.NoteFieldService
import com.gabinote.coffeenote.policy.domain.policy.PolicyKey
import com.gabinote.coffeenote.policy.service.policy.PolicyService
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.field.AttributeTestDataHelper
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldType
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteFieldTestDataHelper
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows

class NoteFieldServiceTest : ServiceTestTemplate() {
    lateinit var noteFieldService: NoteFieldService

    @MockK
    lateinit var attributeService: AttributeService

    @MockK
    lateinit var noteFieldMapper: NoteFieldMapper

    @MockK
    lateinit var policyService: PolicyService

    init {
        beforeTest {
            clearAllMocks()
            noteFieldService = NoteFieldService(
                attributeService = attributeService,
                noteFieldMapper = noteFieldMapper,
                policyService = policyService
            )
        }

        describe("[Note] NoteFieldService Test") {
            describe("NoteFieldService.create(single)") {

                describe("성공 케이스") {
                    context("올바른 NoteFieldCreateReqServiceDto 가 주어지면") {
                        val req = NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto()
                        // attribute 생성
                        val attributes = AttributeTestDataHelper.createTestAttribute()

                        beforeTest {
                            every {
                                attributeService.createAttribute(
                                    fieldType = req.type,
                                    attributesCreateReq = req.attributes
                                )
                            } returns setOf(attributes)
                        }

                        // 노트 필드 생성 및 attribute 설정
                        val createdNoteField = mockk<NoteField>()

                        beforeTest {
                            every {
                                noteFieldMapper.toNoteField(
                                    dto = req
                                )
                            } returns createdNoteField

                            every {
                                createdNoteField.changeAttributes(setOf(attributes))
                            } returns Unit
                        }

                        it("노트 필드가 생성되어 반환된다.") {

                            val result = noteFieldService.create(req)

                            result shouldBe createdNoteField

                            verify(exactly = 1) {
                                attributeService.createAttribute(
                                    fieldType = req.type,
                                    attributesCreateReq = req.attributes
                                )
                                noteFieldMapper.toNoteField(req)
                            }
                        }
                    }
                }

                describe("실패 케이스") {
                    context("올바르지 않은 Value가 주어지면") {
                        val req = NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                            values = setOf(TestFieldType.INVALID_VALUE)
                        )
                        // attribute 생성
                        val attributes = AttributeTestDataHelper.createTestAttribute()

                        beforeTest {
                            every {
                                attributeService.createAttribute(
                                    fieldType = req.type,
                                    attributesCreateReq = req.attributes
                                )
                            } returns setOf(attributes)
                        }



                        it("ResourceNotValid 예외가 발생한다.") {

                            val ex = assertThrows<ResourceNotValid> {
                                noteFieldService.create(req)
                            }

                        }
                    }
                }

            }

            describe("NoteFieldService.create(List)") {

                describe("성공 케이스") {
                    context("올바른 NoteFieldCreateReqServiceDto 가 주어지면") {
                        val req = NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                            id = "anyId",
                            order = 0
                        )
                        val req2 = NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                            id = "anyId2",
                            order = 1,
                            attributes = setOf(
                                AttributeTestDataHelper.createTestAttributeCreateReqServiceDto(
                                    key = "isValid123123123",
                                    value = setOf("true123123")
                                )
                            )
                        )
                        // 검증
                        // 1. 표시 필드 수량
                        beforeTest {
                            every {
                                policyService.getByKey(PolicyKey.NOTE_MAX_DISPLAYED_FIELD_COUNT)
                            } returns "10"
                        }
                        // attribute 생성
                        val attributes1 = AttributeTestDataHelper.createTestAttribute()
                        val attributes2 = AttributeTestDataHelper.createTestAttribute()
                        beforeTest {
                            every {
                                attributeService.createAttribute(
                                    fieldType = req.type,
                                    attributesCreateReq = req.attributes
                                )
                            } returns setOf(attributes1)

                            every {
                                attributeService.createAttribute(
                                    fieldType = req2.type,
                                    attributesCreateReq = req2.attributes
                                )
                            } returns setOf(attributes2)
                        }

                        // 노트 필드 생성 및 attribute 설정
                        val createdNoteField = mockk<NoteField>()
                        val createdNoteField2 = mockk<NoteField>()
                        beforeTest {
                            every {
                                noteFieldMapper.toNoteField(
                                    dto = req
                                )
                            } returns createdNoteField


                            every {
                                noteFieldMapper.toNoteField(
                                    dto = req2
                                )
                            } returns createdNoteField2

                        }

                        it("노트 필드가 생성되어 반환된다.") {

                            val result = noteFieldService.create(listOf(req, req2))

                            result shouldBe listOf(createdNoteField, createdNoteField2)

                            verify(exactly = 1) {
                                policyService.getByKey(PolicyKey.NOTE_MAX_DISPLAYED_FIELD_COUNT)
                                attributeService.createAttribute(
                                    fieldType = req.type,
                                    attributesCreateReq = req.attributes
                                )
                                noteFieldMapper.toNoteField(req)

                                attributeService.createAttribute(
                                    fieldType = req2.type,
                                    attributesCreateReq = req2.attributes
                                )
                                noteFieldMapper.toNoteField(req2)
                            }
                        }
                    }
                }

                describe("실패 케이스") {
                    describe("잘못된 Value 케이스") {
                        context("올바르지 않은 Value가 들어있는 NoteFieldCreateReqServiceDto 가 주어지면") {
                            val req = NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                                id = "anyId",
                                order = 0
                            )
                            //invalid value 포함
                            val invalidReq = NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                                id = "anyId2",
                                order = 1,
                                attributes = setOf(
                                    AttributeTestDataHelper.createTestAttributeCreateReqServiceDto(
                                        key = "isValid123123123",
                                        value = setOf("123132213")
                                    )
                                ),
                                values = setOf(TestFieldType.INVALID_VALUE) // invalid value
                            )
                            // 검증
                            // 1. 표시 필드 수량
                            beforeTest {
                                every {
                                    policyService.getByKey(PolicyKey.NOTE_MAX_DISPLAYED_FIELD_COUNT)
                                } returns "10"
                            }
                            // attribute 생성
                            val attributes1 = AttributeTestDataHelper.createTestAttribute()
                            val attributes2 = AttributeTestDataHelper.createTestAttribute()
                            beforeTest {
                                every {
                                    attributeService.createAttribute(
                                        fieldType = req.type,
                                        attributesCreateReq = req.attributes
                                    )
                                } returns setOf(attributes1)

                                every {
                                    attributeService.createAttribute(
                                        fieldType = invalidReq.type,
                                        attributesCreateReq = invalidReq.attributes
                                    )
                                } returns setOf(attributes2)
                            }

                            // 첫번째 값만 정상 수행
                            val createdNoteField = mockk<NoteField>()

                            beforeTest {
                                every {
                                    noteFieldMapper.toNoteField(
                                        dto = req
                                    )
                                } returns createdNoteField

                            }

                            it("ResourceNotValid 예외가 발생한다.") {

                                val ex = assertThrows<ResourceNotValid> {
                                    noteFieldService.create(listOf(req, invalidReq))
                                }

                                ex.name shouldBe "Note Field Values"


                                verify(exactly = 1) {
                                    policyService.getByKey(PolicyKey.NOTE_MAX_DISPLAYED_FIELD_COUNT)
                                    attributeService.createAttribute(
                                        fieldType = req.type,
                                        attributesCreateReq = req.attributes
                                    )
                                    noteFieldMapper.toNoteField(req)

                                    attributeService.createAttribute(
                                        fieldType = invalidReq.type,
                                        attributesCreateReq = invalidReq.attributes
                                    )

                                }
                            }
                        }
                    }

                    describe("잘못된 Order 케이스") {
                        context("불연속적인 Order가 들어있는 NoteFieldCreateReqServiceDto 리스트가 주어지면") {
                            val req = NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                                id = "anyId",
                                order = 0
                            )
                            val req2 = NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                                id = "anyId2",
                                order = 3, // 불연속적인 order
                                attributes = setOf(
                                    AttributeTestDataHelper.createTestAttributeCreateReqServiceDto(
                                        key = "isValid123123123",
                                        value = setOf("true123123")
                                    )
                                )
                            )


                            it("ResourceNotValid 예외가 발생한다.") {
                                val ex = assertThrows<ResourceNotValid> {
                                    noteFieldService.create(listOf(req, req2))
                                }
                                ex.name shouldBe "NoteField"
                                ex.reasons shouldBe listOf("Order values must be a continuous sequence starting from 0 to 1.")
                            }
                        }
                        context("중복된 Order가 들어있는 NoteFieldCreateReqServiceDto 리스트가 주어지면") {
                            val req = NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                                id = "anyId",
                                order = 0
                            )
                            val req2 = NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                                id = "anyId2",
                                order = 0, // 중복된 order
                                attributes = setOf(
                                    AttributeTestDataHelper.createTestAttributeCreateReqServiceDto(
                                        key = "isValid123123123",
                                        value = setOf("true123123")
                                    )
                                )
                            )
                            it("ResourceNotValid 예외가 발생한다.") {
                                val ex = assertThrows<ResourceNotValid> {
                                    noteFieldService.create(listOf(req, req2))
                                }
                                ex.name shouldBe "NoteField"
                                ex.reasons shouldBe listOf("Duplicate order values are not allowed.")
                            }
                        }
                    }
                    describe("잘못된 표시 필드 수 케이스") {
                        context("표시 필드 수가 정책에서 정한 최대값을 초과하는 NoteFieldCreateReqServiceDto 리스트가 주어지면") {
                            val req = NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                                id = "anyId",
                                order = 0,
                                isDisplay = true
                            )
                            val req2 = NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                                id = "anyId2",
                                order = 1,
                                attributes = setOf(
                                    AttributeTestDataHelper.createTestAttributeCreateReqServiceDto(
                                        key = "isValid123123123",
                                        value = setOf("true123123")
                                    )
                                ),
                                isDisplay = true
                            )

                            beforeTest {
                                every {
                                    policyService.getByKey(PolicyKey.NOTE_MAX_DISPLAYED_FIELD_COUNT)
                                } returns "1" // 최대 표시 필드 수를 1로 설정
                            }


                            it("ResourceNotValid 예외가 발생한다.") {
                                val ex = assertThrows<ResourceNotValid> {
                                    noteFieldService.create(listOf(req, req2))
                                }
                                ex.name shouldBe "NoteField"
                                ex.reasons shouldBe listOf("The number of display fields exceeds the maximum allowed count of 1.")
                            }
                        }
                    }

                    describe("잘못된 ID 케이스") {
                        context("중복된 ID가 들어있는 NoteFieldCreateReqServiceDto 리스트가 주어지면") {
                            val req = NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                                id = "anyId", // 중복 ID
                                order = 0
                            )
                            val req2 = NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                                id = "anyId", // 중복 ID
                                order = 1,
                                attributes = setOf(
                                    AttributeTestDataHelper.createTestAttributeCreateReqServiceDto(
                                        key = "isValid123123123",
                                        value = setOf("true123123")
                                    )
                                )
                            )
                            // 검증
                            // 1. 표시 필드 수량
                            beforeTest {
                                every {
                                    policyService.getByKey(PolicyKey.NOTE_MAX_DISPLAYED_FIELD_COUNT)
                                } returns "10"
                            }


                            it("노트 필드가 생성되어 반환된다.") {

                                val ex = assertThrows<ResourceNotValid> {
                                    noteFieldService.create(listOf(req, req2))
                                }

                                ex.name shouldBe "NoteField"
                                ex.reasons shouldBe listOf("Duplicate field IDs are not allowed.")


                                verify(exactly = 1) {
                                    policyService.getByKey(PolicyKey.NOTE_MAX_DISPLAYED_FIELD_COUNT)

                                }
                            }
                        }
                    }

                }

            }
        }
    }
}