package com.gabinote.coffeenote.note.service.note

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotFound
import com.gabinote.coffeenote.common.util.exception.service.ResourceQuotaLimit
import com.gabinote.coffeenote.note.domain.note.*
import com.gabinote.coffeenote.note.dto.note.service.NoteListResServiceDto
import com.gabinote.coffeenote.note.dto.note.service.NoteResServiceDto
import com.gabinote.coffeenote.note.dto.note.vo.NoteExtIdHash
import com.gabinote.coffeenote.note.mapping.note.NoteMapper
import com.gabinote.coffeenote.note.service.note.strategy.GetNoteByExternalIdStrategyFactory
import com.gabinote.coffeenote.note.service.note.strategy.GetNoteByExternalIdStrategyType
import com.gabinote.coffeenote.note.service.note.strategy.GetNoteOpenStrategy
import com.gabinote.coffeenote.note.service.noteDisplayField.NoteDisplayFieldService
import com.gabinote.coffeenote.note.service.noteField.NoteFieldService
import com.gabinote.coffeenote.note.service.noteHash.NoteHashService
import com.gabinote.coffeenote.policy.domain.policy.PolicyKey
import com.gabinote.coffeenote.policy.service.policy.PolicyService
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteHashTestDataHelper
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteTestDataHelper
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteTestDataHelper.createTestNote
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteTestDataHelper.createTestNoteCreateReqServiceDto
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteTestDataHelper.createTestOwnedItem
import com.gabinote.coffeenote.testSupport.testUtil.page.TestPageableUtil.createPageable
import com.gabinote.coffeenote.testSupport.testUtil.page.TestSliceUtil.toSlice
import com.gabinote.coffeenote.testSupport.testUtil.time.TestTimeProvider
import com.gabinote.coffeenote.testSupport.testUtil.uuid.TestUuidSource
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class NoteServiceTest : ServiceTestTemplate() {

    private lateinit var noteService: NoteService

    @MockK
    private lateinit var noteRepository: NoteRepository

    @MockK
    private lateinit var noteMapper: NoteMapper

    @MockK
    private lateinit var getNoteByExternalIdStrategyFactory: GetNoteByExternalIdStrategyFactory

    @MockK
    private lateinit var noteFieldService: NoteFieldService

    @MockK
    private lateinit var policyService: PolicyService

    @MockK
    private lateinit var noteHashService: NoteHashService

    @MockK
    private lateinit var noteDisplayFieldService: NoteDisplayFieldService

    init {
        beforeTest {
            clearAllMocks()
            noteService = NoteService(
                noteRepository,
                noteMapper,
                getNoteByExternalIdStrategyFactory,
                noteFieldService,
                policyService,
                noteHashService,
                noteDisplayFieldService
            )
        }

        describe("[Note] NoteService Test") {
            describe("NoteService.fetchById") {
                context("존재하는 Note id가 주어지면,") {
                    val existingNoteId = ObjectId()

                    val existingNote = createTestNote(existingNoteId)

                    beforeTest {
                        every {
                            noteRepository.findByIdOrNull(existingNoteId)
                        } returns existingNote
                    }

                    it("해당 id에 맞는 Note를 반환한다.") {
                        val result = noteService.fetchById(existingNoteId)
                        result.id shouldBe existingNoteId

                        verify(exactly = 1) { noteRepository.findByIdOrNull(existingNoteId) }
                    }
                }

                context("존재하지 않는 Note id가 주어지면,") {
                    val nonExistingNoteId = ObjectId()

                    beforeTest {
                        every {
                            noteRepository.findByIdOrNull(nonExistingNoteId)
                        } returns null
                    }

                    it("ResourceNotFound 예외를 던진다.") {
                        val ex = assertThrows<ResourceNotFound> { noteService.fetchById(nonExistingNoteId) }
                        ex.name shouldBe "Note"
                        ex.identifier shouldBe nonExistingNoteId.toString()
                        ex.identifierType shouldBe "id"
                        verify(exactly = 1) { noteRepository.findByIdOrNull(nonExistingNoteId) }
                    }
                }
            }

            describe("NoteService.fetchByExternalId") {
                context("존재하는 Note externalId가 주어지면,") {
                    val existingNoteExternalId = TestUuidSource.UUID_STRING

                    val existingNote = createTestNote(externalId = existingNoteExternalId.toString())

                    beforeTest {
                        every {
                            noteRepository.findByExternalId(existingNoteExternalId.toString())
                        } returns existingNote
                    }

                    it("해당 externalId에 맞는 Note를 반환한다.") {
                        val result = noteService.fetchByExternalId(existingNoteExternalId)
                        result.externalId shouldBe existingNoteExternalId.toString()

                        verify(exactly = 1) { noteRepository.findByExternalId(existingNoteExternalId.toString()) }
                    }
                }

                context("존재하지 않는 Note externalId가 주어지면,") {
                    val nonExistingNoteExternalId = java.util.UUID.randomUUID()

                    beforeTest {
                        every {
                            noteRepository.findByExternalId(nonExistingNoteExternalId.toString())
                        } returns null
                    }

                    it("ResourceNotFound 예외를 던진다.") {
                        val ex =
                            assertThrows<ResourceNotFound> { noteService.fetchByExternalId(nonExistingNoteExternalId) }
                        ex.name shouldBe "Note"
                        ex.identifier shouldBe nonExistingNoteExternalId.toString()
                        ex.identifierType shouldBe "externalId"
                        verify(exactly = 1) { noteRepository.findByExternalId(nonExistingNoteExternalId.toString()) }
                    }
                }
            }



            describe("NoteService.getByExternalId") {
                context("올바른 전략과 함께 존재하는 Note externalId가 주어지면,") {
                    val existingNoteExternalId = TestUuidSource.UUID_STRING
                    val targetNote = createTestNote(externalId = existingNoteExternalId.toString())
                    val testRequestor = "test-requestor"
                    // 어떤 전략을 사용하던 해당 테스트 케이스에서는 상관 없음.
                    // 전략 테스트는 전략 테스트에서 다룸.
                    val testStrategy = GetNoteByExternalIdStrategyType.OPENED
                    val mockStrategy = mockk<GetNoteOpenStrategy>()
                    val noteResServiceDto = mockk<NoteResServiceDto>()
                    beforeTest {
                        every {
                            noteRepository.findByExternalId(existingNoteExternalId.toString())
                        } returns targetNote

                        every {
                            getNoteByExternalIdStrategyFactory.getStrategy(testStrategy)
                        } returns mockStrategy

                        every {
                            mockStrategy.validate(requestor = testRequestor, note = targetNote)
                        } returns Unit

                        every {
                            noteMapper.toNoteResServiceDto(targetNote)
                        } returns noteResServiceDto
                    }

                    it("해당 externalId에 맞는 Note를 전략에 따라 검증 후 반환한다.") {
                        val res = noteService.getByExternalId(
                            externalId = TestUuidSource.UUID_STRING,
                            requestor = testRequestor,
                            strategyType = testStrategy
                        )

                        res shouldBe noteResServiceDto

                        verify(exactly = 1) { noteRepository.findByExternalId(existingNoteExternalId.toString()) }
                        verify(exactly = 1) {
                            getNoteByExternalIdStrategyFactory.getStrategy(testStrategy)
                        }
                        verify(exactly = 1) {
                            mockStrategy.validate(requestor = testRequestor, note = targetNote)
                        }

                        verify(exactly = 1) {
                            noteMapper.toNoteResServiceDto(targetNote)
                        }
                    }
                }
            }

            describe("NoteService.getOpenByExternalId") {
                context("올바른 정보가 주어지면,") {
                    val existingNoteExternalId = TestUuidSource.UUID_STRING
                    val targetNote = createTestNote(externalId = existingNoteExternalId.toString())
                    val testRequestor = "test-requestor"
                    // 어떤 전략을 사용하던 해당 테스트 케이스에서는 상관 없음.
                    // 전략 테스트는 전략 테스트에서 다룸.
                    val testStrategy = GetNoteByExternalIdStrategyType.OPENED
                    val mockStrategy = mockk<GetNoteOpenStrategy>()
                    val noteResServiceDto = mockk<NoteResServiceDto>()
                    beforeTest {
                        every {
                            noteRepository.findByExternalId(existingNoteExternalId.toString())
                        } returns targetNote

                        every {
                            getNoteByExternalIdStrategyFactory.getStrategy(testStrategy)
                        } returns mockStrategy

                        every {
                            mockStrategy.validate(requestor = testRequestor, note = targetNote)
                        } returns Unit

                        every {
                            noteMapper.toNoteResServiceDto(targetNote)
                        } returns noteResServiceDto
                    }

                    it("해당 externalId에 맞는 Note를 전략에 따라 검증 후 반환한다.") {
                        val res = noteService.getOpenByExternalId(
                            externalId = TestUuidSource.UUID_STRING,
                            requestor = testRequestor,
                        )

                        res shouldBe noteResServiceDto

                        verify(exactly = 1) { noteRepository.findByExternalId(existingNoteExternalId.toString()) }
                        verify(exactly = 1) {
                            getNoteByExternalIdStrategyFactory.getStrategy(testStrategy)
                        }
                        verify(exactly = 1) {
                            mockStrategy.validate(requestor = testRequestor, note = targetNote)
                        }

                        verify(exactly = 1) {
                            noteMapper.toNoteResServiceDto(targetNote)
                        }
                    }
                }
            }

            describe("NoteService.getOwnedByExternalId") {
                context("올바른 정보가 주어지면,") {
                    val existingNoteExternalId = TestUuidSource.UUID_STRING
                    val targetNote = createTestNote(externalId = existingNoteExternalId.toString())
                    val testRequestor = "test-requestor"
                    // 어떤 전략을 사용하던 해당 테스트 케이스에서는 상관 없음.
                    // 전략 테스트는 전략 테스트에서 다룸.
                    val testStrategy = GetNoteByExternalIdStrategyType.OWNED
                    val mockStrategy = mockk<GetNoteOpenStrategy>()
                    val noteResServiceDto = mockk<NoteResServiceDto>()
                    beforeTest {
                        every {
                            noteRepository.findByExternalId(existingNoteExternalId.toString())
                        } returns targetNote

                        every {
                            getNoteByExternalIdStrategyFactory.getStrategy(testStrategy)
                        } returns mockStrategy

                        every {
                            mockStrategy.validate(requestor = testRequestor, note = targetNote)
                        } returns Unit

                        every {
                            noteMapper.toNoteResServiceDto(targetNote)
                        } returns noteResServiceDto
                    }

                    it("해당 externalId에 맞는 Note를 전략에 따라 검증 후 반환한다.") {
                        val res = noteService.getOwnedByExternalId(
                            externalId = TestUuidSource.UUID_STRING,
                            requestor = testRequestor
                        )

                        res shouldBe noteResServiceDto

                        verify(exactly = 1) { noteRepository.findByExternalId(existingNoteExternalId.toString()) }
                        verify(exactly = 1) {
                            getNoteByExternalIdStrategyFactory.getStrategy(testStrategy)
                        }
                        verify(exactly = 1) {
                            mockStrategy.validate(requestor = testRequestor, note = targetNote)
                        }

                        verify(exactly = 1) {
                            noteMapper.toNoteResServiceDto(targetNote)
                        }
                    }
                }
            }

            describe("NoteService.getAllByOwner") {
                context("올바른 정보가 주어지면,") {
                    val testRequestor = "test-requestor"
                    val testPageable = createPageable()
                    val note1 = createTestOwnedItem(owner = testRequestor)
                    val slicedNote = listOf(note1).toSlice(testPageable)

                    val noteRes = mockk<NoteListResServiceDto>()

                    beforeTest {
                        every {
                            noteRepository.findAllByOwnerAndStatus(
                                owner = testRequestor,
                                pageable = testPageable,
                                status = NoteStatus.ACTIVE
                            )
                        } returns slicedNote

                        every {
                            noteMapper.toListResServiceDto(note1)
                        } returns noteRes
                    }

                    it("해당 owner에 맞는 Note 목록을 반환한다.") {
                        val res = noteService.getAllByOwner(
                            owner = testRequestor,
                            pageable = testPageable
                        )

                        res.content.size shouldBe 1
                        res.content[0] shouldBe noteRes

                        verify(exactly = 1) {
                            noteRepository.findAllByOwnerAndStatus(
                                owner = testRequestor,
                                pageable = testPageable,
                                status = NoteStatus.ACTIVE
                            )
                        }

                        verify(exactly = 1) {
                            noteMapper.toListResServiceDto(note1)
                        }
                    }
                }
            }

            describe("NoteService.create") {
                describe("성공 케이스") {
                    context("올바른 정보가 주어지면,") {
                        val owner = "test-owner"
                        val createReq = createTestNoteCreateReqServiceDto(owner = owner)
                        // 유저 노트 수량 체크
                        val maxCount = "100"
                        val userNote = 99L

                        beforeTest {
                            every {
                                policyService.getByKey(PolicyKey.NOTE_MAX_COUNT_PER_DEFAULT_USER)
                            } returns maxCount

                            every {
                                noteRepository.countByOwner(owner)
                            } returns userNote
                        }

                        //노트 엔티티 생성
                        //1. 필드 생성
                        val fields = mockk<NoteField>()
                        beforeTest {
                            every {
                                noteFieldService.create(createReq.fields)
                            } returns listOf(fields)
                        }

                        //2. 표시 필드 생성
                        val displayFields = mockk<NoteDisplayField>()
                        beforeTest {
                            every {
                                noteDisplayFieldService.create(createReq.fields)
                            } returns listOf(displayFields)
                        }

                        //3. 매퍼로 노트 엔티티 생성
                        val noteEntity = mockk<Note>()
                        beforeTest {
                            every {
                                noteMapper.toNote(createReq)
                            } returns noteEntity

                            every {
                                noteEntity.setFields(
                                    listOf(fields),
                                    listOf(displayFields)
                                )
                            } returns Unit
                        }

                        //4. 노트 해쉬 생성
                        beforeTest {
                            every {
                                noteHashService.create(noteEntity)
                            } returns NoteHashTestDataHelper.TEST_HASH

                            every {
                                noteEntity.changeHash(NoteHashTestDataHelper.TEST_HASH)
                            } returns Unit
                        }
                        //노트 저장
                        val savedNote = mockk<Note>()
                        beforeTest {
                            every {
                                noteRepository.save(noteEntity)
                            } returns savedNote
                        }


                        //노트 응답 매퍼
                        val noteResServiceDto = mockk<NoteResServiceDto>()
                        beforeTest {
                            every {
                                noteMapper.toNoteResServiceDto(savedNote)
                            } returns noteResServiceDto
                        }

                        it("노트를 생성하고 반환한다.") {
                            val res = noteService.create(createReq)
                            res shouldBe noteResServiceDto

                            verify(exactly = 1) {
                                policyService.getByKey(PolicyKey.NOTE_MAX_COUNT_PER_DEFAULT_USER)
                            }

                            verify(exactly = 1) {
                                noteRepository.countByOwner(owner)
                            }

                            verify(exactly = 1) {
                                noteFieldService.create(createReq.fields)
                            }

                            verify(exactly = 1) {
                                noteDisplayFieldService.create(createReq.fields)
                            }

                            verify(exactly = 1) {
                                noteMapper.toNote(createReq)
                            }

                            verify(exactly = 1) {
                                noteHashService.create(noteEntity)
                            }

                            verify(exactly = 1) {
                                noteEntity.changeHash(NoteHashTestDataHelper.TEST_HASH)
                            }

                            verify(exactly = 1) {
                                noteEntity.setFields(
                                    listOf(fields),
                                    listOf(displayFields)
                                )
                            }

                            verify(exactly = 1) {
                                noteRepository.save(noteEntity)
                            }

                            verify(exactly = 1) {
                                noteMapper.toNoteResServiceDto(savedNote)
                            }
                        }

                    }
                }

                describe("실패 케이스") {
                    context("최대 노트 개수를 초과하면,") {
                        val owner = "test-owner"
                        val createReq = createTestNoteCreateReqServiceDto(owner = owner)
                        // 유저 노트 수량 체크
                        val maxCount = "100"
                        val userNote = 100L

                        beforeTest {
                            every {
                                policyService.getByKey(PolicyKey.NOTE_MAX_COUNT_PER_DEFAULT_USER)
                            } returns maxCount

                            every {
                                noteRepository.countByOwner(owner)
                            } returns userNote
                        }


                        it("ResourceQuotaLimit 예외를 던진다.") {
                            val ex = assertThrows<ResourceQuotaLimit> {
                                noteService.create(createReq)
                            }

                            ex.name shouldBe "Note"
                            ex.quotaType shouldBe "Max Note Count Per User"
                            ex.quotaLimit shouldBe 100L


                            verify(exactly = 1) {
                                policyService.getByKey(PolicyKey.NOTE_MAX_COUNT_PER_DEFAULT_USER)
                            }

                            verify(exactly = 1) {
                                noteRepository.countByOwner(owner)
                            }

                        }

                    }
                }
            }

            describe("NoteService.update") {
                describe("성공 케이스") {
                    context("올바른 정보가 주어지면,") {
                        val owner = "test-owner"
                        val existingNoteExternalId = TestUuidSource.UUID_STRING
                        val updateReq = NoteTestDataHelper.createTestNoteUpdateReqServiceDto(
                            owner = owner,
                            externalId = existingNoteExternalId
                        )
                        //a. 기존 노트 조회
                        val existingNote = createTestNote(
                            externalId = existingNoteExternalId.toString(),
                            owner = owner,
                            hash = "old-hash"
                        )
                        beforeTest {
                            every {
                                noteRepository.findByExternalId(existingNoteExternalId.toString())
                            } returns existingNote
                        }
                        //b. 소유권 확인

                        //c. 생성 Dto 변환 후 생성

                        //c-0. 생성 Dto 변환
                        val createReq = createTestNoteCreateReqServiceDto()
                        beforeTest {
                            every {
                                noteMapper.toCreateReqServiceDto(updateReq)
                            } returns createReq

                        }
                        //c-1. 필드 생성
                        val fields = mockk<NoteField>()
                        beforeTest {
                            every {
                                noteFieldService.create(createReq.fields)
                            } returns listOf(fields)
                        }

                        //c-2. 표시 필드 생성
                        val displayFields = mockk<NoteDisplayField>()
                        beforeTest {
                            every {
                                noteDisplayFieldService.create(createReq.fields)
                            } returns listOf(displayFields)
                        }

                        //c-3. 매퍼로 노트 엔티티 생성
                        val noteEntity = createTestNote()
                        beforeTest {
                            every {
                                noteMapper.toNote(createReq)
                            } returns noteEntity

                        }

                        //c-4. 해시 생성 및 적용
                        val newHash = "new-hash"
                        beforeTest {
                            every {
                                noteHashService.create(noteEntity)
                            } returns newHash

                        }

                        //d. 해쉬 비교하여 변경 감지


                        //e. 생성된 노트로 기존 노트 업데이트
                        beforeTest {
                            every {
                                noteMapper.updateNoteFromEntity(source = noteEntity, target = existingNote)
                            } returns Unit
                        }

                        //f. 노트 저장
                        val updatedNote = mockk<Note>()
                        beforeTest {
                            every {
                                noteRepository.save(existingNote)
                            } returns updatedNote
                        }

                        //h. dto로 변환
                        val noteResServiceDto = mockk<NoteResServiceDto>()
                        beforeTest {
                            every {
                                noteMapper.toNoteResServiceDto(updatedNote)
                            } returns noteResServiceDto
                        }
                        it("노트를 수정하고 반환한다.") {
                            val res = noteService.update(updateReq)
                            res shouldBe noteResServiceDto

                            //a
                            verify(exactly = 1) {
                                noteRepository.findByExternalId(existingNoteExternalId.toString())
                            }

                            //c-0
                            verify(exactly = 1) {
                                noteMapper.toCreateReqServiceDto(updateReq)
                            }

                            //c-1
                            verify(exactly = 1) {
                                noteFieldService.create(createReq.fields)
                            }

                            //c-2
                            verify(exactly = 1) {
                                noteDisplayFieldService.create(createReq.fields)
                            }

                            //c-3
                            verify(exactly = 1) {
                                noteMapper.toNote(createReq)
                            }

                            //c-4
                            verify(exactly = 1) {
                                noteHashService.create(noteEntity)
                            }

                            //e
                            verify(exactly = 1) {
                                noteMapper.updateNoteFromEntity(source = noteEntity, target = existingNote)
                            }

                            //f
                            verify(exactly = 1) {
                                noteRepository.save(existingNote)
                            }

                            //h
                            verify(exactly = 1) {
                                noteMapper.toNoteResServiceDto(updatedNote)
                            }
                        }
                    }

                    context("동일한 해시를 가진 노트로 업데이트하면,(변경사항이 없으면)") {
                        val owner = "test-owner"
                        val existingNoteExternalId = TestUuidSource.UUID_STRING
                        val updateReq = NoteTestDataHelper.createTestNoteUpdateReqServiceDto(
                            owner = owner,
                            externalId = existingNoteExternalId
                        )
                        //a. 기존 노트 조회
                        val existingNote = createTestNote(
                            externalId = existingNoteExternalId.toString(),
                            owner = owner,
                            hash = "old-hash"
                        )
                        beforeTest {
                            every {
                                noteRepository.findByExternalId(existingNoteExternalId.toString())
                            } returns existingNote
                        }
                        //b. 소유권 확인

                        //c. 생성 Dto 변환 후 생성

                        //c-0. 생성 Dto 변환
                        val createReq = createTestNoteCreateReqServiceDto()
                        beforeTest {
                            every {
                                noteMapper.toCreateReqServiceDto(updateReq)
                            } returns createReq

                        }
                        //c-1. 필드 생성
                        val fields = mockk<NoteField>()
                        beforeTest {
                            every {
                                noteFieldService.create(createReq.fields)
                            } returns listOf(fields)
                        }

                        //c-2. 표시 필드 생성
                        val displayFields = mockk<NoteDisplayField>()
                        beforeTest {
                            every {
                                noteDisplayFieldService.create(createReq.fields)
                            } returns listOf(displayFields)
                        }

                        //c-3. 매퍼로 노트 엔티티 생성
                        val noteEntity = createTestNote()
                        beforeTest {
                            every {
                                noteMapper.toNote(createReq)
                            } returns noteEntity

                        }

                        //c-4. 해시 생성 및 적용
                        //hash 동일
                        val newHash = existingNote.hash
                        beforeTest {
                            every {
                                noteHashService.create(noteEntity)
                            } returns newHash!!

                        }

                        //d. 해쉬 비교하여 변경 감지
                        // 여기서 동일 해쉬 판단으로 기존값 리턴해야함
                        val noteResServiceDto = mockk<NoteResServiceDto>()
                        beforeTest {
                            every {
                                noteMapper.toNoteResServiceDto(existingNote)
                            } returns noteResServiceDto
                        }

                        it("노트를 수정하고 반환한다.") {
                            val res = noteService.update(updateReq)
                            res shouldBe noteResServiceDto

                            //a
                            verify(exactly = 1) {
                                noteRepository.findByExternalId(existingNoteExternalId.toString())
                            }

                            //c-0
                            verify(exactly = 1) {
                                noteMapper.toCreateReqServiceDto(updateReq)
                            }

                            //c-1
                            verify(exactly = 1) {
                                noteFieldService.create(createReq.fields)
                            }

                            //c-2
                            verify(exactly = 1) {
                                noteDisplayFieldService.create(createReq.fields)
                            }

                            //c-3
                            verify(exactly = 1) {
                                noteMapper.toNote(createReq)
                            }

                            //c-4
                            verify(exactly = 1) {
                                noteHashService.create(noteEntity)
                            }


                        }
                    }
                }

                describe("실패 케이스") {
                    context("본인 소유가 아닌 노트가 주어지면,") {
                        val owner = "test-owner"
                        val existingNoteExternalId = TestUuidSource.UUID_STRING
                        val updateReq = NoteTestDataHelper.createTestNoteUpdateReqServiceDto(
                            owner = owner,
                            externalId = existingNoteExternalId
                        )
                        // 기존 노트 조회
                        val existingNote = createTestNote(externalId = existingNoteExternalId.toString(), owner = "doh")
                        beforeTest {
                            every {
                                noteRepository.findByExternalId(existingNoteExternalId.toString())
                            } returns existingNote
                        }


                        it("노트를 수정하고 반환한다.") {
                            val ex = assertThrows<ResourceNotFound> {
                                noteService.update(updateReq)
                            }

                            ex.name shouldBe "Note"
                            ex.identifier shouldBe updateReq.externalId.toString()
                            ex.identifierType shouldBe "externalId"


                            verify(exactly = 1) {
                                noteRepository.findByExternalId(updateReq.externalId.toString())
                            }
                        }
                    }
                }
            }

            describe("NoteService.deleteByExternalId") {
                context("본인 소유인 노트 externalId가 주어지면,") {
                    val owner = "test-owner"
                    val existingNoteExternalId = TestUuidSource.UUID_STRING

                    // 기존 노트 조회
                    val existingNote = createTestNote(externalId = existingNoteExternalId.toString(), owner = owner)
                    beforeTest {
                        every {
                            noteRepository.findByExternalId(existingNoteExternalId.toString())
                        } returns existingNote
                    }

                    beforeTest {
                        every {
                            noteRepository.delete(existingNote)
                        } returns Unit
                    }

                    it("해당 externalId에 맞는 Note를 삭제한다.") {
                        noteService.deleteByExternalId(
                            externalId = existingNoteExternalId,
                            owner = owner
                        )

                        verify(exactly = 1) {
                            noteRepository.findByExternalId(existingNoteExternalId.toString())
                        }

                        verify(exactly = 1) {
                            noteRepository.delete(existingNote)
                        }
                    }
                }

                context("본인 소유가 아닌 노트 externalId가 주어지면,") {
                    val owner = "test-owner"
                    val existingNoteExternalId = TestUuidSource.UUID_STRING

                    // 기존 노트 조회
                    val existingNote = createTestNote(externalId = existingNoteExternalId.toString(), owner = "doh")
                    beforeTest {
                        every {
                            noteRepository.findByExternalId(existingNoteExternalId.toString())
                        } returns existingNote
                    }
                    it("ResourceNotFound 예외를 던진다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            noteService.deleteByExternalId(
                                externalId = existingNoteExternalId,
                                owner = owner
                            )
                        }

                        ex.name shouldBe "Note"
                        ex.identifier shouldBe existingNoteExternalId.toString()
                        ex.identifierType shouldBe "externalId"

                        verify(exactly = 1) {
                            noteRepository.findByExternalId(existingNoteExternalId.toString())
                        }
                    }
                }
            }

            describe("NoteService.softDeleteByExternalId") {
                context("본인 소유인 노트 externalId가 주어지면,") {
                    val owner = "test-owner"
                    val existingNoteExternalId = TestUuidSource.UUID_STRING

                    // 기존 노트 조회
                    val existingNote = createTestNote(externalId = existingNoteExternalId.toString(), owner = owner)
                    beforeTest {
                        every {
                            noteRepository.findByExternalId(existingNoteExternalId.toString())
                        } returns existingNote
                    }

                    existingNote.wipeData()

                    beforeTest {
                        every {
                            noteRepository.save(existingNote)
                        } returns existingNote
                    }

                    it("해당 externalId에 맞는 Note를 소프트 삭제한다.") {
                        noteService.softDeleteByExternalId(
                            externalId = existingNoteExternalId,
                            owner = owner
                        )

                        verify(exactly = 1) {
                            noteRepository.findByExternalId(existingNoteExternalId.toString())
                        }

                        verify(exactly = 1) {
                            noteRepository.save(existingNote)
                        }

                    }
                }

                context("본인 소유가 아닌 노트 externalId가 주어지면,") {
                    val owner = "test-owner"
                    val existingNoteExternalId = TestUuidSource.UUID_STRING

                    // 기존 노트 조회
                    val existingNote = createTestNote(externalId = existingNoteExternalId.toString(), owner = "doh")
                    beforeTest {
                        every {
                            noteRepository.findByExternalId(existingNoteExternalId.toString())
                        } returns existingNote
                    }
                    it("ResourceNotFound 예외를 던진다.") {
                        val ex = assertThrows<ResourceNotFound> {
                            noteService.deleteByExternalId(
                                externalId = existingNoteExternalId,
                                owner = owner
                            )
                        }

                        ex.name shouldBe "Note"
                        ex.identifier shouldBe existingNoteExternalId.toString()
                        ex.identifierType shouldBe "externalId"

                        verify(exactly = 1) {
                            noteRepository.findByExternalId(existingNoteExternalId.toString())
                        }
                    }
                }
            }

            describe("NoteService.deleteAllByOwner") {
                context("특정 소유자의 노트 삭제가 요청되면,") {
                    val owner = "test-owner"

                    beforeTest {
                        every {
                            noteRepository.deleteAllByOwner(owner)
                        } returns Unit
                    }

                    it("해당 소유자의 모든 노트를 삭제한다.") {
                        noteService.deleteAllByOwner(owner)

                        verify(exactly = 1) {
                            noteRepository.deleteAllByOwner(owner)
                        }
                    }
                }
            }

            describe("NoteService.getAllNoteExtIdHashWithBetweenModifiedDate") {
                context("수정일자 기준으로 특정 기간 내의 모든 노트를 조회하면,") {
                    val startDate = TestTimeProvider.testDateTime
                    val endDate = TestTimeProvider.testDateTime
                    val pageable = createPageable()

                    beforeTest {
                        every {
                            noteRepository.findAllByModifiedDateBetween(
                                startDate = startDate,
                                endDate = endDate,
                                pageable = pageable,
                                type = NoteExtIdHash::class.java
                            )
                        } returns listOf()
                    }

                    it("해당 기간에 수정된 모든 노트의 externalId와 hash를 반환한다.") {
                        noteService.getAllNoteExtIdHashWithBetweenModifiedDate(
                            startDate = startDate,
                            endDate = endDate,
                            pageable = pageable
                        )

                        verify(exactly = 1) {
                            noteRepository.findAllByModifiedDateBetween(
                                startDate = startDate,
                                endDate = endDate,
                                pageable = pageable,
                                type = NoteExtIdHash::class.java
                            )
                        }
                    }
                }

            }

            describe("NoteService.getCountWithBetweenModifiedDate") {
                context("수정일자 기준으로 특정 기간 내의 모든 노트를 조회하면,") {
                    val startDate = TestTimeProvider.testDateTime
                    val endDate = TestTimeProvider.testDateTime

                    beforeTest {
                        every {
                            noteRepository.countAllByModifiedDateBetween(
                                startDate = startDate,
                                endDate = endDate,
                            )
                        } returns 2
                    }

                    it("해당 기간에 수정된 모든 노트의 갯수를 반환한다.") {
                        noteService.getCountWithBetweenModifiedDate(
                            startDate = startDate,
                            endDate = endDate
                        )

                        verify(exactly = 1) {
                            noteRepository.countAllByModifiedDateBetween(
                                startDate = startDate,
                                endDate = endDate,
                            )
                        }
                    }
                }

            }

            describe("NoteService.getCountBeforeModifiedDate") {
                context("수정일자 기준으로 특정 일자 이전의 모든 노트를 조회하면,") {
                    val beforeDate = TestTimeProvider.testDateTime

                    beforeTest {
                        every {
                            noteRepository.countAllByModifiedDateBefore(
                                beforeDate = beforeDate
                            )
                        } returns 2
                    }

                    it("해당 일자 이전에 수정된 모든 노트의 externalId와 hash를 반환한다.") {
                        noteService.getCountBeforeModifiedDate(
                            beforeDate = beforeDate
                        )

                        verify(exactly = 1) {
                            noteRepository.countAllByModifiedDateBefore(
                                beforeDate = beforeDate
                            )
                        }
                    }
                }
            }
        }
    }
}