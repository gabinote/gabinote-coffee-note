package com.gabinote.coffeenote.note.mapping.noteIndex

import com.gabinote.coffeenote.common.mapping.time.TimeMapper
import com.gabinote.coffeenote.common.mapping.time.TimeMapperImpl
import com.gabinote.coffeenote.note.domain.noteIndex.IndexDisplayField
import com.gabinote.coffeenote.note.domain.noteIndex.NoteIndex
import com.gabinote.coffeenote.note.dto.noteIndex.controller.NoteIndexResControllerDto
import com.gabinote.coffeenote.note.dto.noteIndex.service.NoteIndexResServiceDto
import com.gabinote.coffeenote.note.dto.noteIndexDisplayField.controller.IndexDisplayFieldResControllerDto
import com.gabinote.coffeenote.note.dto.noteIndexDisplayField.service.IndexDisplayFieldResServiceDto
import com.gabinote.coffeenote.note.mapping.noteIndexDisplayField.NoteIndexDisplayFieldMapper
import com.gabinote.coffeenote.note.mapping.noteIndexDisplayField.NoteIndexDisplayFieldMapperImpl
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.time.TestTimeProvider
import com.gabinote.coffeenote.testSupport.testUtil.uuid.TestUuidSource
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [NoteIndexMapperImpl::class, NoteIndexDisplayFieldMapperImpl::class, TimeMapperImpl::class])
class NoteIndexMapperTest : MockkTestTemplate() {
    @Autowired
    lateinit var noteIndexMapper: NoteIndexMapper

    @MockkBean
    lateinit var noteIndexDisplayFieldMapper: NoteIndexDisplayFieldMapper

    @MockkBean
    lateinit var timeMapper: TimeMapper

    init {
        describe("[Note] NoteIndexMapper Test") {
            describe("NoteIndexMapper.toResServiceDto") {
                context("NoteIndex가 주어지면 ") {
                    val displayField = mockk<IndexDisplayField>()
                    val filters = mockk<Map<String, List<String>>>()
                    val noteIndex = NoteIndex(
                        id = "some-id",
                        externalId = TestUuidSource.UUID_STRING.toString(),
                        title = "Test Note Index",
                        createdDate = TestTimeProvider.testEpochSecond,
                        modifiedDate = TestTimeProvider.testEpochSecond,
                        displayFields = listOf(
                            displayField
                        ),
                        filters = filters,
                        owner = "test-owner"
                    )
                    val displayFieldResDto = mockk<IndexDisplayFieldResServiceDto>()
                    beforeTest {
                        every {
                            noteIndexDisplayFieldMapper.toDisplayFieldResServiceDto(displayField)
                        } returns displayFieldResDto

                        every {
                            timeMapper.toDateTime(TestTimeProvider.testEpochSecond)
                        } returns TestTimeProvider.testDateTime
                    }

                    val expected = NoteIndexResServiceDto(
                        id = noteIndex.id,
                        externalId = noteIndex.externalId,
                        title = noteIndex.title,
                        owner = noteIndex.owner,
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        displayFields = listOf(displayFieldResDto),
                        filters = noteIndex.filters
                    )

                    it("NoteIndexResServiceDto로 변환되어야 한다.") {

                        val result = noteIndexMapper.toResServiceDto(noteIndex)
                        result shouldBe expected
                        verify(exactly = 1) {
                            noteIndexDisplayFieldMapper.toDisplayFieldResServiceDto(displayField)
                        }

                        verify(exactly = 2) {
                            timeMapper.toDateTime(TestTimeProvider.testEpochSecond)
                        }

                    }
                }

            }
            describe("NoteIndexMapper.toResControllerDto") {
                context("NoteIndexResServiceDto가 주어지면 ") {
                    val displayFieldResDto = mockk<IndexDisplayFieldResServiceDto>()
                    val noteIndexResServiceDto = NoteIndexResServiceDto(
                        id = "some-id",
                        externalId = TestUuidSource.UUID_STRING.toString(),
                        title = "Test Note Index",
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        displayFields = listOf(
                            displayFieldResDto
                        ),
                        filters = emptyMap(),
                        owner = "test-owner"
                    )

                    val displayFieldResControllerDto = mockk<IndexDisplayFieldResControllerDto>()

                    beforeTest {
                        every {
                            noteIndexDisplayFieldMapper.toDisplayFieldResControllerDto(displayFieldResDto)
                        } returns displayFieldResControllerDto
                    }

                    val expected = NoteIndexResControllerDto(
                        externalId = noteIndexResServiceDto.externalId,
                        title = noteIndexResServiceDto.title,
                        owner = noteIndexResServiceDto.owner,
                        createdDate = noteIndexResServiceDto.createdDate,
                        modifiedDate = noteIndexResServiceDto.modifiedDate,
                        displayFields = listOf(displayFieldResControllerDto),
                        filters = noteIndexResServiceDto.filters
                    )

                    it("NoteIndexResControllerDto로 변환되어야 한다.") {

                        val result = noteIndexMapper.toResControllerDto(noteIndexResServiceDto)
                        result shouldBe expected
                        verify(exactly = 1) {
                            noteIndexDisplayFieldMapper.toDisplayFieldResControllerDto(displayFieldResDto)
                        }
                    }
                }
            }
        }
    }
}
