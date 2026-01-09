package com.gabinote.coffeenote.note.service.note.strategy

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotFound
import com.gabinote.coffeenote.note.domain.note.NoteStatus
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteTestDataHelper
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows

class GetNoteByExternalIdStrategyTest : ServiceTestTemplate() {

    lateinit var openStrategy: GetNoteOpenStrategy

    lateinit var ownedStrategy: GetNoteOwnedStrategy

    lateinit var factory: GetNoteByExternalIdStrategyFactory

    init {
        beforeTest {
            openStrategy = GetNoteOpenStrategy()
            ownedStrategy = GetNoteOwnedStrategy()
            factory = GetNoteByExternalIdStrategyFactory(
                strategies = listOf(
                    openStrategy,
                    ownedStrategy
                )
            )
        }

        describe("[Note] GetNoteByExternalIdStrategy Test") {

            describe("Factory Test") {
                it("GetNoteByExternalIdStrategyType.OPENED 전략을 요청하면 GetNoteOpenStrategy 가 반환된다.") {
                    // when
                    val strategy = factory.getStrategy(GetNoteByExternalIdStrategyType.OPENED)

                    // then
                    strategy shouldBe openStrategy
                }

                it("GetNoteByExternalIdStrategyType.OWNED 전략을 요청하면 GetNoteOwnedStrategy 가 반환된다.") {
                    // when
                    val strategy = factory.getStrategy(GetNoteByExternalIdStrategyType.OWNED)

                    // then
                    strategy shouldBe ownedStrategy
                }
            }

            describe("Strategy Type Test") {
                describe("GetNoteOpenStrategy") {
                    describe("type 테스트") {
                        context("GetNoteOpenStrategy 의 type 을 조회하면") {
                            // when
                            val type = openStrategy.type

                            // then
                            it("GetNoteByExternalIdStrategyType.OPENED 가 반환된다.") {
                                type shouldBe GetNoteByExternalIdStrategyType.OPENED
                            }
                        }
                    }
                    describe("validate 테스트") {
                        context("공개 노트가 주어지면") {
                            val requestor = "anyUser"
                            val note = NoteTestDataHelper.createTestNote(isOpen = true)
                            it("예외가 발생하지 않는다.") {
                                openStrategy.validate(requestor, note)
                            }
                        }

                        context("비공개 노트가 주어지면") {
                            val requestor = "anyUser"
                            val note = NoteTestDataHelper.createTestNote(isOpen = false)
                            it("ResourceNotFound 예외가 발생한다.") {
                                val ex = assertThrows<ResourceNotFound> {
                                    openStrategy.validate(requestor, note)
                                }

                                ex.name shouldBe "Opened Note"
                                ex.identifier shouldBe note.externalId.toString()
                                ex.identifierType shouldBe "externalId"
                            }
                        }

                        context("DELETE 상태의 노트가 주어지면") {
                            val requestor = "anyUser"
                            val note = NoteTestDataHelper.createTestNote(
                                isOpen = true,
                                status = NoteStatus.DELETED
                            )
                            it("ResourceNotFound 예외가 발생한다.") {
                                val ex = assertThrows<ResourceNotFound> {
                                    openStrategy.validate(requestor, note)
                                }

                                ex.name shouldBe "Opened Note"
                                ex.identifier shouldBe note.externalId.toString()
                                ex.identifierType shouldBe "externalId"
                            }
                        }
                    }

                }
                describe("GetNoteOwnedStrategy") {
                    describe("type 테스트") {
                        context("GetNoteOwnedStrategy 의 type 을 조회하면") {
                            // when
                            val type = ownedStrategy.type

                            // then
                            it("GetNoteByExternalIdStrategyType.OWNED 가 반환된다.") {
                                type shouldBe GetNoteByExternalIdStrategyType.OWNED
                            }
                        }
                    }
                    describe("validate 테스트") {
                        context("소유자가 요청자인 노트가 주어지면") {
                            val requestor = "ownerUser"
                            val note = NoteTestDataHelper.createTestNote(owner = "ownerUser")
                            it("예외가 발생하지 않는다.") {
                                ownedStrategy.validate(requestor, note)
                            }
                        }

                        context("소유자가 아닌 사용자가 요청자인 노트가 주어지면") {
                            val requestor = "otherUser"
                            val note = NoteTestDataHelper.createTestNote(owner = "ownerUser")
                            it("ResourceNotFound 예외가 발생한다.") {
                                val ex = assertThrows<ResourceNotFound> {
                                    ownedStrategy.validate(requestor, note)
                                }

                                ex.name shouldBe "Owned Note"
                                ex.identifier shouldBe note.externalId.toString()
                                ex.identifierType shouldBe "externalId"
                            }
                        }

                        context("DELETE 상태의 노트가 주어지면") {
                            val requestor = "anyUser"
                            val note = NoteTestDataHelper.createTestNote(
                                isOpen = true,
                                status = NoteStatus.DELETED
                            )
                            it("ResourceNotFound 예외가 발생한다.") {
                                val ex = assertThrows<ResourceNotFound> {
                                    ownedStrategy.validate(requestor, note)
                                }

                                ex.name shouldBe "Owned Note"
                                ex.identifier shouldBe note.externalId.toString()
                                ex.identifierType shouldBe "externalId"
                            }
                        }
                    }

                }
            }


        }
    }

}