package com.gabinote.coffeenote.note.event.userWithdraw

import com.gabinote.coffeenote.note.service.note.NoteService
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.uuid.TestUuidSource
import com.gabinote.coffeenote.user.event.userWithdraw.UserWithdrawEvent
import io.mockk.*
import io.mockk.impl.annotations.MockK

class NoteUserWithdrawEventListenerTest : ServiceTestTemplate() {

    lateinit var noteUserWithdrawEventListener: NoteUserWithdrawEventListener

    @MockK
    lateinit var noteService: NoteService

    init {
        beforeTest {
            clearAllMocks()
            noteUserWithdrawEventListener = NoteUserWithdrawEventListener(noteService)
        }

        describe("[Note] NoteUserWithdrawEventListener Test") {
            describe("NoteUserWithdrawEventListener.handleUserWithdrawEvent") {
                context("정상적인 탈퇴 이벤트가 주어졌을 때") {
                    val uid = TestUuidSource.UUID_STRING
                    val event = UserWithdrawEvent(uid = uid)

                    beforeTest {
                        every { noteService.deleteAllByOwner(uid.toString()) } just runs
                    }

                    it("사용자의 모든 노트를 삭제한다") {
                        noteUserWithdrawEventListener.handleUserWithdrawEvent(event)

                        verify(exactly = 1) {
                            noteService.deleteAllByOwner(uid.toString())
                        }
                    }
                }

                context("노트 삭제 중 예외가 발생했을 때") {
                    val uid = TestUuidSource.UUID_STRING
                    val event = UserWithdrawEvent(uid = uid)

                    beforeTest {
                        every { noteService.deleteAllByOwner(uid.toString()) } throws RuntimeException("delete failed")
                    }

                    it("예외를 외부로 던지지 않고 처리한다") {
                        noteUserWithdrawEventListener.handleUserWithdrawEvent(event)

                        verify(exactly = 1) {
                            noteService.deleteAllByOwner(uid.toString())
                        }
                    }
                }
            }
        }
    }
}
