package com.gabinote.coffeenote.note.service.noteDisplayField

import com.gabinote.coffeenote.note.mapping.noteDisplayField.NoteDisplayFieldMapper
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteDisplayFieldTestDataHelper
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteFieldTestDataHelper
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify

class NoteDisplayFieldServiceTest : ServiceTestTemplate() {

    lateinit var noteDisplayFieldService: NoteDisplayFieldService

    @MockK
    lateinit var noteDisplayFieldMapper: NoteDisplayFieldMapper

    init {
        beforeTest {
            clearAllMocks()
            noteDisplayFieldService = NoteDisplayFieldService(
                noteDisplayFieldMapper = noteDisplayFieldMapper
            )
        }

        describe("[Note] NoteDisplayFieldService Test") {
            describe("NoteDisplayFieldService.create") {
                context("올바른 NoteFieldCreateReqServiceDto 리스트가 주어지면") {
                    val displayField1 =
                        NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                            id = "1",
                            order = 0,
                            name = "원산지",
                            isDisplay = true
                        )
                    val displayField2 =
                        NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                            id = "2",
                            order = 1,
                            isDisplay = false
                        ) // 무시되야함
                    val displayField3 =
                        NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                            id = "3",
                            order = 2,
                            isDisplay = false
                        ) // 무시되야함

                    val displayField4 =
                        NoteFieldTestDataHelper.createTestNoteFieldCreateReqServiceDto(
                            id = "4",
                            order = 3,
                            name = "가공방식",
                            isDisplay = true
                        ) // order가 1로 재정렬되야함

                    val inputList = listOf(displayField1, displayField2, displayField3, displayField4)

                    val expected1 = NoteDisplayFieldTestDataHelper.createTestNoteDisplayField(order = 0, name = "원산지")
                    val expected2 = NoteDisplayFieldTestDataHelper.createTestNoteDisplayField(order = 1, name = "가공방식")

                    beforeTest {
                        every {
                            noteDisplayFieldMapper.toDisplayField(displayField1, 0)
                        } returns expected1

                        every {
                            noteDisplayFieldMapper.toDisplayField(displayField4, 1)
                        } returns expected2
                    }

                    it("NoteDisplayField 리스트가 반환된다.") {

                        val result = noteDisplayFieldService.create(inputList)

                        result.size shouldBe 2
                        result[0] shouldBe expected1
                        result[1] shouldBe expected2

                        verify(exactly = 1) {
                            noteDisplayFieldMapper.toDisplayField(displayField1, 0)
                        }
                        verify(exactly = 1) {
                            noteDisplayFieldMapper.toDisplayField(displayField4, 1)
                        }
                    }
                }
            }
        }
    }
}