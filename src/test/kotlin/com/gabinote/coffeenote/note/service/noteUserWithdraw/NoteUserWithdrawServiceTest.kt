package com.gabinote.coffeenote.note.service.noteUserWithdraw

import com.gabinote.coffeenote.note.event.userWithdraw.WithdrawProcess
import com.gabinote.coffeenote.note.service.note.NoteService
import com.gabinote.coffeenote.note.service.noteFieldIndex.NoteFieldIndexService
import com.gabinote.coffeenote.note.service.noteIndex.NoteIndexService
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.coffeenote.user.service.withdrawProcessHistory.WithdrawProcessHistoryService
import io.mockk.*
import io.mockk.impl.annotations.MockK

class NoteUserWithdrawServiceTest : ServiceTestTemplate() {

    lateinit var noteUserWithdrawService: NoteUserWithdrawService

    @MockK
    lateinit var noteService: NoteService

    @MockK
    lateinit var withdrawProcessHistoryService: WithdrawProcessHistoryService

    @MockK
    lateinit var noteFieldIndexService: NoteFieldIndexService

    @MockK
    lateinit var noteIndexService: NoteIndexService

    init {
        beforeTest {
            clearAllMocks()
            noteUserWithdrawService = NoteUserWithdrawService(
                noteService = noteService,
                withdrawProcessHistoryService = withdrawProcessHistoryService,
                noteIndexService = noteIndexService,
                noteFieldIndexService = noteFieldIndexService,
            )
        }

        describe("[Note] NoteUserWithdrawService Test") {

            describe("NoteUserWithdrawService.deleteAllNotesByWithdrawUser") {
                context("유효한 uid가 주어졌을 때") {
                    val uid = "test-user-uid"

                    beforeTest {
                        every { noteService.deleteAllByOwner(uid) } just runs
                        every {
                            withdrawProcessHistoryService.create(
                                uid = uid,
                                process = WithdrawProcess.NOTE_DELETE
                            )
                        } just runs
                    }

                    it("사용자의 모든 노트를 삭제하고 성공 이력을 기록한다") {
                        noteUserWithdrawService.deleteAllNotesByWithdrawUser(uid)

                        verify(exactly = 1) {
                            noteService.deleteAllByOwner(uid)
                        }
                        verify(exactly = 1) {
                            withdrawProcessHistoryService.create(
                                uid = uid,
                                process = WithdrawProcess.NOTE_DELETE
                            )
                        }
                    }
                }

                context("노트가 없는 사용자의 uid가 주어졌을 때") {
                    val uid = "user-without-notes"

                    beforeTest {
                        every { noteService.deleteAllByOwner(uid) } just runs
                        every {
                            withdrawProcessHistoryService.create(
                                uid = uid,
                                process = WithdrawProcess.NOTE_DELETE
                            )
                        } just runs
                    }

                    it("삭제 작업을 수행하고 성공 이력을 기록한다") {
                        noteUserWithdrawService.deleteAllNotesByWithdrawUser(uid)

                        verify(exactly = 1) {
                            noteService.deleteAllByOwner(uid)
                        }
                        verify(exactly = 1) {
                            withdrawProcessHistoryService.create(
                                uid = uid,
                                process = WithdrawProcess.NOTE_DELETE
                            )
                        }
                    }
                }
            }

            describe("NoteUserWithdrawService.deleteAllNoteIndexesByWithdrawUser") {
                context("유효한 uid가 주어졌을 때") {
                    val uid = "test-user-uid"

                    beforeTest {
                        every { noteIndexService.deleteAllByOwner(uid) } just runs
                        every {
                            withdrawProcessHistoryService.create(
                                uid = uid,
                                process = WithdrawProcess.NOTE_INDEX_DELETE
                            )
                        } just runs
                    }

                    it("사용자의 모든 노트 인덱스를 삭제하고 성공 이력을 기록한다") {
                        noteUserWithdrawService.deleteAllNoteIndexesByWithdrawUser(uid)

                        verify(exactly = 1) {
                            noteIndexService.deleteAllByOwner(uid)
                        }
                        verify(exactly = 1) {
                            withdrawProcessHistoryService.create(
                                uid = uid,
                                process = WithdrawProcess.NOTE_INDEX_DELETE
                            )
                        }
                    }
                }
            }

            describe("NoteUserWithdrawService.deleteAllNoteFieldsIndexesByWithdrawUser") {
                context("유효한 uid가 주어졌을 때") {
                    val uid = "test-user-uid"

                    beforeTest {
                        every { noteFieldIndexService.deleteAllByOwner(uid) } just runs
                        every {
                            withdrawProcessHistoryService.create(
                                uid = uid,
                                process = WithdrawProcess.NOTE_FIELD_INDEX_DELETE
                            )
                        } just runs
                    }

                    it("사용자의 모든 노트 필드 인덱스를 삭제하고 성공 이력을 기록한다") {
                        noteUserWithdrawService.deleteAllNoteFieldsIndexesByWithdrawUser(uid)

                        verify(exactly = 1) {
                            noteFieldIndexService.deleteAllByOwner(uid)
                        }
                        verify(exactly = 1) {
                            withdrawProcessHistoryService.create(
                                uid = uid,
                                process = WithdrawProcess.NOTE_FIELD_INDEX_DELETE
                            )
                        }
                    }
                }
            }

            describe("NoteUserWithdrawService.createDeleteNoteFailHistory") {
                context("유효한 uid가 주어졌을 때") {
                    val uid = "test-user-uid"

                    beforeTest {
                        every {
                            withdrawProcessHistoryService.create(
                                uid = uid,
                                process = WithdrawProcess.NOTE_DELETE,
                                isPassed = false
                            )
                        } just runs
                    }

                    it("노트 삭제 실패 이력을 기록한다") {
                        noteUserWithdrawService.createDeleteNoteFailHistory(uid, WithdrawProcess.NOTE_DELETE)

                        verify(exactly = 1) {
                            withdrawProcessHistoryService.create(
                                uid = uid,
                                process = WithdrawProcess.NOTE_DELETE,
                                isPassed = false
                            )
                        }
                    }
                }
            }
        }
    }
}

