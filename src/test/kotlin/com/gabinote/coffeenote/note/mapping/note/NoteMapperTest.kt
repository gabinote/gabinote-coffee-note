package com.gabinote.coffeenote.note.mapping.note

import com.gabinote.coffeenote.note.domain.note.Note
import com.gabinote.coffeenote.note.domain.note.NoteDisplayField
import com.gabinote.coffeenote.note.domain.note.NoteField
import com.gabinote.coffeenote.note.domain.note.NoteStatus
import com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto
import com.gabinote.coffeenote.note.dto.note.controller.NoteListResControllerDto
import com.gabinote.coffeenote.note.dto.note.controller.NoteResControllerDto
import com.gabinote.coffeenote.note.dto.note.controller.NoteUpdateReqControllerDto
import com.gabinote.coffeenote.note.dto.note.service.NoteCreateReqServiceDto
import com.gabinote.coffeenote.note.dto.note.service.NoteListResServiceDto
import com.gabinote.coffeenote.note.dto.note.service.NoteResServiceDto
import com.gabinote.coffeenote.note.dto.note.service.NoteUpdateReqServiceDto
import com.gabinote.coffeenote.note.dto.note.vo.NoteOwnedItem
import com.gabinote.coffeenote.note.dto.noteDisplayField.controller.NoteDisplayFieldResControllerDto
import com.gabinote.coffeenote.note.dto.noteDisplayField.service.NoteDisplayFieldResServiceDto
import com.gabinote.coffeenote.note.dto.noteField.controller.NoteFieldCreateReqControllerDto
import com.gabinote.coffeenote.note.dto.noteField.controller.NoteFieldResControllerDto
import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldCreateReqServiceDto
import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldResServiceDto
import com.gabinote.coffeenote.note.mapping.noteDisplayField.NoteDisplayFieldMapper
import com.gabinote.coffeenote.note.mapping.noteDisplayField.NoteDisplayFieldMapperImpl
import com.gabinote.coffeenote.note.mapping.noteField.NoteFieldMapper
import com.gabinote.coffeenote.note.mapping.noteField.NoteFieldMapperImpl
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteHashTestDataHelper
import com.gabinote.coffeenote.testSupport.testUtil.time.TestTimeProvider
import com.gabinote.coffeenote.testSupport.testUtil.uuid.TestUuidSource
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime
import java.util.*

@ContextConfiguration(
    classes = [
        NoteMapperImpl::class,
        NoteDisplayFieldMapperImpl::class,
        NoteFieldMapperImpl::class,
    ]
)
class NoteMapperTest : MockkTestTemplate() {

    @Autowired
    lateinit var noteMapper: NoteMapper

    @MockkBean
    lateinit var noteFieldMapper: NoteFieldMapper

    @MockkBean
    lateinit var noteDisplayFieldMapper: NoteDisplayFieldMapper

    init {
        describe("[Note] NoteMapper Test") {

            describe("NoteMapper.toNoteResServiceDto") {
                context("Note 엔티티가 주어지면,") {

                    val noteField = mockk<NoteField>()
                    val displayField = mockk<NoteDisplayField>()

                    val note = Note(
                        id = ObjectId(),
                        externalId = TestUuidSource.UUID_STRING.toString(),
                        title = "Test Note",
                        thumbnail = "test-thumbnail.jpg",
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        fields = listOf(noteField),
                        displayFields = listOf(displayField),
                        isOpen = true,
                        owner = "test-owner",
                        hash = NoteHashTestDataHelper.TEST_HASH
                    )

                    val expectedNoteField = mockk<NoteFieldResServiceDto>()
                    val expectedDisplayField = mockk<NoteDisplayFieldResServiceDto>()

                    beforeTest {
                        every {
                            noteFieldMapper.toResServiceDto(noteField)
                        } returns expectedNoteField

                        every {
                            noteDisplayFieldMapper.toResServiceDto(displayField)
                        } returns expectedDisplayField
                    }
                    val expected = NoteResServiceDto(
                        id = note.id!!,
                        externalId = UUID.fromString(note.externalId),
                        title = note.title,
                        thumbnail = note.thumbnail,
                        createdDate = note.createdDate!!,
                        modifiedDate = note.modifiedDate!!,
                        fields = listOf(expectedNoteField),
                        displayFields = listOf(expectedDisplayField),
                        isOpen = note.isOpen,
                        owner = note.owner,
                        status = note.status,
                        hash = note.hash!!,
                    )

                    it("NoteResServiceDto로 변환되어야 한다.") {
                        val result = noteMapper.toNoteResServiceDto(note)

                        result shouldBe expected

                        verify(exactly = 1) {
                            noteFieldMapper.toResServiceDto(noteField)
                            noteDisplayFieldMapper.toResServiceDto(displayField)
                        }

                    }
                }

                context("fields와 displayFields가 빈 Note 엔티티가 주어지면,") {

                    val note = Note(
                        id = ObjectId(),
                        externalId = TestUuidSource.UUID_STRING.toString(),
                        title = "Simple Note",
                        thumbnail = null,
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        fields = emptyList(),
                        displayFields = emptyList(),
                        isOpen = false,
                        owner = "test-owner",
                        hash = NoteHashTestDataHelper.TEST_HASH
                    )

                    val expected = NoteResServiceDto(
                        id = note.id!!,
                        externalId = UUID.fromString(note.externalId),
                        title = note.title,
                        thumbnail = note.thumbnail,
                        createdDate = note.createdDate!!,
                        modifiedDate = note.modifiedDate!!,
                        fields = emptyList(),
                        displayFields = emptyList(),
                        isOpen = note.isOpen,
                        owner = note.owner,
                        status = note.status,
                        hash = note.hash!!,
                    )

                    it("빈 필드들을 가진 NoteResServiceDto로 변환되어야 한다.") {
                        val result = noteMapper.toNoteResServiceDto(note)
                        result shouldBe expected

                        verify(exactly = 0) {
                            noteFieldMapper.toResServiceDto(any())
                            noteDisplayFieldMapper.toResServiceDto(any())
                        }

                    }
                }
            }

            describe("NoteMapper.toNoteListResServiceDto") {
                context("NoteOwnedItem이 주어지면,") {

                    val displayField = mockk<NoteDisplayField>()
                    val noteOwnedItem = NoteOwnedItem(
                        id = ObjectId(),
                        externalId = TestUuidSource.UUID_STRING.toString(),
                        title = "Owned Note",
                        thumbnail = "thumbnail.jpg",
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        displayFields = listOf(displayField),
                        isOpen = true,
                        owner = "test-owner",
                        status = NoteStatus.ACTIVE,
                    )

                    val displayFieldRes = mockk<NoteDisplayFieldResServiceDto>()

                    beforeTest {
                        every {
                            noteDisplayFieldMapper.toResServiceDto(displayField)
                        } returns displayFieldRes
                    }

                    val expected = NoteListResServiceDto(
                        id = noteOwnedItem.id,
                        externalId = UUID.fromString(noteOwnedItem.externalId),
                        title = noteOwnedItem.title,
                        thumbnail = noteOwnedItem.thumbnail,
                        createdDate = noteOwnedItem.createdDate!!,
                        modifiedDate = noteOwnedItem.modifiedDate!!,
                        displayFields = listOf(displayFieldRes),
                        isOpen = noteOwnedItem.isOpen,
                        owner = noteOwnedItem.owner
                    )

                    it("NoteListResServiceDto로 변환되어야 한다.") {
                        val result = noteMapper.toListResServiceDto(noteOwnedItem)
                        result shouldBe expected

                        verify(exactly = 1) {
                            noteDisplayFieldMapper.toResServiceDto(displayField)
                        }
                    }
                }
            }

            describe("NoteMapper.toResControllerDto") {
                context("NoteResServiceDto가 주어지면,") {
                    val field = mockk<NoteFieldResServiceDto>()
                    val displayField = mockk<NoteDisplayFieldResServiceDto>()

                    val dto = NoteResServiceDto(
                        id = ObjectId(),
                        externalId = UUID.randomUUID(),
                        title = "Test Note",
                        thumbnail = "test-thumbnail.jpg",
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        fields = listOf(field),
                        displayFields = listOf(displayField),
                        isOpen = true,
                        owner = "test-owner",
                        status = NoteStatus.ACTIVE,
                        hash = NoteHashTestDataHelper.TEST_HASH
                    )

                    val fieldRes = mockk<NoteFieldResControllerDto>()
                    val displayFieldRes = mockk<NoteDisplayFieldResControllerDto>()

                    beforeTest {
                        every {
                            noteFieldMapper.toResControllerDto(field)
                        } returns fieldRes

                        every {
                            noteDisplayFieldMapper.toResControllerDto(displayField)
                        } returns displayFieldRes

                    }
                    val expected = NoteResControllerDto(
                        externalId = dto.externalId,
                        title = dto.title,
                        thumbnail = dto.thumbnail,
                        createdDate = dto.createdDate,
                        modifiedDate = dto.modifiedDate,
                        fields = listOf(fieldRes),
                        displayFields = listOf(displayFieldRes),
                        isOpen = dto.isOpen,
                        owner = dto.owner
                    )

                    it("NoteResControllerDto로 변환되어야 한다.") {
                        val result = noteMapper.toResControllerDto(dto)
                        result shouldBe expected
                        verify(exactly = 1) {
                            noteFieldMapper.toResControllerDto(field)
                            noteDisplayFieldMapper.toResControllerDto(displayField)
                        }
                    }
                }

                context("fields와 displayFields가 빈 NoteResServiceDto가 주어지면,") {


                    val dto = NoteResServiceDto(
                        id = ObjectId(),
                        externalId = UUID.randomUUID(),
                        title = "Test Note",
                        thumbnail = "test-thumbnail.jpg",
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        fields = listOf(),
                        displayFields = listOf(),
                        isOpen = true,
                        owner = "test-owner",
                        status = NoteStatus.ACTIVE,
                        hash = NoteHashTestDataHelper.TEST_HASH
                    )

                    val expected = NoteResControllerDto(
                        externalId = dto.externalId,
                        title = dto.title,
                        thumbnail = dto.thumbnail,
                        createdDate = dto.createdDate,
                        modifiedDate = dto.modifiedDate,
                        fields = listOf(),
                        displayFields = listOf(),
                        isOpen = dto.isOpen,
                        owner = dto.owner
                    )

                    it("NoteResControllerDto로 변환되어야 한다.") {
                        val result = noteMapper.toResControllerDto(dto)
                        result shouldBe expected
                        verify(exactly = 0) {
                            noteFieldMapper.toResControllerDto(any())
                            noteDisplayFieldMapper.toResControllerDto(any())
                        }
                    }
                }
            }

            describe("NoteMapper.toListResControllerDto") {
                context("NoteListResServiceDto가 주어지면,") {
                    val displayFieldDto = mockk<NoteDisplayFieldResServiceDto>()

                    val dto = NoteListResServiceDto(
                        id = ObjectId(),
                        externalId = UUID.randomUUID(),
                        title = "List Note",
                        thumbnail = "thumbnail.jpg",
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        displayFields = listOf(displayFieldDto),
                        isOpen = true,
                        owner = "test-owner"
                    )

                    val displayFieldRes = mockk<NoteDisplayFieldResControllerDto>()

                    beforeTest {
                        every {
                            noteDisplayFieldMapper.toResControllerDto(displayFieldDto)
                        } returns displayFieldRes

                    }
                    val expected = NoteListResControllerDto(
                        externalId = dto.externalId,
                        title = dto.title,
                        thumbnail = dto.thumbnail,
                        createdDate = dto.createdDate,
                        modifiedDate = dto.modifiedDate,
                        isOpen = dto.isOpen,
                        owner = dto.owner,
                        displayFields = listOf(displayFieldRes)
                    )

                    it("NoteListResControllerDto로 변환되어야 한다.") {
                        val result = noteMapper.toListResControllerDto(dto)
                        result shouldBe expected
                        verify(exactly = 1) {
                            noteDisplayFieldMapper.toResControllerDto(displayFieldDto)
                        }
                    }
                }

                context("DisplayFields 가 빈 NoteListResServiceDto가 주어지면,") {

                    val dto = NoteListResServiceDto(
                        id = ObjectId(),
                        externalId = UUID.randomUUID(),
                        title = "List Note",
                        thumbnail = "thumbnail.jpg",
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        displayFields = listOf(),
                        isOpen = true,
                        owner = "test-owner"
                    )


                    val expected = NoteListResControllerDto(
                        externalId = dto.externalId,
                        title = dto.title,
                        thumbnail = dto.thumbnail,
                        createdDate = dto.createdDate,
                        modifiedDate = dto.modifiedDate,
                        isOpen = dto.isOpen,
                        owner = dto.owner
                    )

                    it("NoteListResControllerDto로 변환되어야 한다.") {
                        val result = noteMapper.toListResControllerDto(dto)
                        result shouldBe expected
                        verify(exactly = 0) {
                            noteDisplayFieldMapper.toResControllerDto(any())
                        }
                    }
                }
            }

            describe("NoteMapper.toNote") {
                context("NoteCreateReqServiceDto와 필드 리스트가 주어지면,") {

                    val dto = NoteCreateReqServiceDto(
                        title = "New Note",
                        thumbnail = "new-thumbnail.jpg",
                        fields = emptyList(),
                        isOpen = true,
                        owner = "test-owner"
                    )

                    val expected = Note(
                        title = "New Note",
                        thumbnail = "new-thumbnail.jpg",
                        isOpen = true,
                        owner = "test-owner",
                        fields = emptyList(),
                        displayFields = emptyList(),
                        createdDate = null,
                        modifiedDate = null,
                        id = null,
                        externalId = null,
                        hash = null,
                        status = NoteStatus.ACTIVE,
                    )

                    it("Note 엔티티로 변환되어야 한다.") {
                        val result = noteMapper.toNote(dto)

                        result shouldBe expected
                    }
                }
            }

            describe("NoteMapper.toCreateReqServiceDto (from NoteUpdateReqServiceDto)") {
                context("NoteUpdateReqServiceDto가 주어지면,") {
                    val fieldDto = mockk<NoteFieldCreateReqServiceDto>()

                    val dto = NoteUpdateReqServiceDto(
                        externalId = TestUuidSource.UUID_STRING,
                        title = "Updated Note",
                        thumbnail = "updated-thumbnail.jpg",
                        fields = listOf(fieldDto),
                        isOpen = true,
                        owner = "test-owner"
                    )

                    val expected = NoteCreateReqServiceDto(
                        title = "Updated Note",
                        thumbnail = "updated-thumbnail.jpg",
                        fields = listOf(fieldDto),
                        isOpen = true,
                        owner = "test-owner"
                    )

                    it("NoteCreateReqServiceDto로 변환되어야 한다.") {
                        val result = noteMapper.toCreateReqServiceDto(dto)
                        result shouldBe expected
                    }
                }
            }

            describe("NoteMapper.toCreateReqServiceDto (from NoteCreateReqControllerDto)") {
                context("NoteCreateReqControllerDto와 owner가 주어지면,") {
                    val fieldDto = mockk<NoteFieldCreateReqControllerDto>()


                    val owner = "controller-owner"
                    val dto = NoteCreateReqControllerDto(
                        title = "Controller Note",
                        thumbnail = "controller-thumbnail.jpg",
                        fields = listOf(fieldDto),
                        isOpen = false
                    )

                    val fieldServiceDto = mockk<NoteFieldCreateReqServiceDto>()

                    beforeTest {
                        every {
                            noteFieldMapper.toCreateReqServiceDto(fieldDto)
                        } returns fieldServiceDto
                    }

                    val expected = NoteCreateReqServiceDto(
                        title = "Controller Note",
                        thumbnail = "controller-thumbnail.jpg",
                        fields = listOf(fieldServiceDto),
                        isOpen = false,
                        owner = "controller-owner"
                    )

                    it("NoteCreateReqServiceDto로 변환되어야 한다.") {
                        val result = noteMapper.toCreateReqServiceDto(dto, owner)
                        result shouldBe expected
                        verify(exactly = 1) {
                            noteFieldMapper.toCreateReqServiceDto(fieldDto)
                        }
                    }
                }
            }

            describe("NoteMapper.toUpdateReqServiceDto") {
                context("NoteUpdateReqControllerDto, externalId, owner가 주어지면,") {
                    val fieldDto = mockk<NoteFieldCreateReqControllerDto>()
                    val externalId = TestUuidSource.UUID_STRING
                    val owner = "update-owner"

                    val dto = NoteUpdateReqControllerDto(
                        title = "Update Note",
                        thumbnail = "update-thumbnail.jpg",
                        fields = listOf(fieldDto),
                        isOpen = true
                    )

                    val updatedFieldServiceDto = mockk<NoteFieldCreateReqServiceDto>()

                    beforeTest {
                        every {
                            noteFieldMapper.toCreateReqServiceDto(fieldDto)
                        } returns updatedFieldServiceDto
                    }

                    val expected = NoteUpdateReqServiceDto(
                        externalId = externalId,
                        title = "Update Note",
                        thumbnail = "update-thumbnail.jpg",
                        fields = listOf(updatedFieldServiceDto),
                        isOpen = true,
                        owner = owner
                    )

                    it("NoteUpdateReqServiceDto로 변환되어야 한다.") {
                        val result = noteMapper.toUpdateReqServiceDto(dto, externalId, owner)
                        result shouldBe expected
                        verify(exactly = 1) {
                            noteFieldMapper.toCreateReqServiceDto(fieldDto)
                        }
                    }
                }
            }


            describe("NoteMapper.updateNoteFromEntity") {
                context("source Note와 target Note가 주어지면,") {

                    val sourceField = mockk<NoteField>()
                    val sourceDisplayField = mockk<NoteDisplayField>()

                    val sourceNote = Note(
                        id = null,
                        externalId = null,
                        title = "Source Title",
                        thumbnail = "source-thumbnail.jpg",
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        fields = listOf(sourceField),
                        displayFields = listOf(sourceDisplayField),
                        isOpen = true,
                        owner = "source-owner",
                        hash = NoteHashTestDataHelper.TEST_HASH
                    )


                    val targetNote = Note(
                        id = ObjectId(),
                        externalId = TestUuidSource.UUID_STRING.toString(),
                        title = "Target Title",
                        thumbnail = "target-thumbnail.jpg",
                        createdDate = LocalDateTime.of(2023, 1, 1, 0, 0),
                        modifiedDate = LocalDateTime.of(2023, 1, 1, 0, 0),
                        fields = emptyList(),
                        displayFields = emptyList(),
                        isOpen = false,
                        owner = "target-owner",
                        hash = NoteHashTestDataHelper.TEST_HASH
                    )

                    val updatedNote = Note(
                        id = targetNote.id, // 업데이트 X
                        externalId = targetNote.externalId, // 업데이트 X
                        title = "Source Title",
                        thumbnail = "source-thumbnail.jpg",
                        createdDate = targetNote.createdDate, // 업데이트 X
                        modifiedDate = targetNote.modifiedDate, // 업데이트 X
                        fields = listOf(), // 업데이트 X
                        displayFields = listOf(), // 업데이트 X
                        isOpen = true,
                        owner = targetNote.owner, // 업데이트 X
                        hash = NoteHashTestDataHelper.TEST_HASH // 업데이트 X
                    )

                    it("target Note의 내용이 source로 업데이트되지만, id, externalId, owner, dates는 유지되어야 한다.") {
                        noteMapper.updateNoteFromEntity(sourceNote, targetNote)
                        targetNote shouldBe updatedNote

                    }
                }
            }
        }
    }
}

