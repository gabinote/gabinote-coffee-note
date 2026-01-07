package com.gabinote.coffeenote.note.service.noteUserWithdraw

import com.gabinote.coffeenote.note.event.userWithdraw.WithdrawProcess
import com.gabinote.coffeenote.note.service.note.NoteService
import com.gabinote.coffeenote.user.service.withdrawProcessHistory.WithdrawProcessHistoryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NoteUserWithdrawService(
    private val noteService: NoteService,
    private val withdrawProcessHistoryService: WithdrawProcessHistoryService,
) {

    @Transactional
    fun deleteAllNotesByWithdrawUser(uid: String) {
        noteService.deleteAllByOwner(uid)
        withdrawProcessHistoryService.create(
            uid = uid,
            process = WithdrawProcess.NOTE_DELETE
        )
    }

    fun createDeleteNoteFailHistory(uid: String) {
        withdrawProcessHistoryService.create(
            uid = uid,
            process = WithdrawProcess.NOTE_DELETE,
            isPassed = false
        )
    }

}