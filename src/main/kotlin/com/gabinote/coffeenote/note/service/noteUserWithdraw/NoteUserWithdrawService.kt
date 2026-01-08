package com.gabinote.coffeenote.note.service.noteUserWithdraw

import com.gabinote.coffeenote.note.event.userWithdraw.WithdrawProcess
import com.gabinote.coffeenote.note.service.note.NoteService
import com.gabinote.coffeenote.note.service.noteFieldIndex.NoteFieldIndexService
import com.gabinote.coffeenote.note.service.noteIndex.NoteIndexService
import com.gabinote.coffeenote.user.service.withdrawProcessHistory.WithdrawProcessHistoryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NoteUserWithdrawService(
    private val noteService: NoteService,
    private val noteIndexService: NoteIndexService,
    private val noteFieldIndexService: NoteFieldIndexService,
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

    @Transactional
    fun deleteAllNoteIndexesByWithdrawUser(uid: String) {
        withdrawProcessHistoryService.create(
            uid = uid,
            process = WithdrawProcess.NOTE_INDEX_DELETE,
            isPassed = true
        )
        noteIndexService.deleteAllByOwner(uid)

    }

    @Transactional
    fun deleteAllNoteFieldsIndexesByWithdrawUser(uid: String) {
        withdrawProcessHistoryService.create(
            uid = uid,
            process = WithdrawProcess.NOTE_FIELD_INDEX_DELETE,
            isPassed = true
        )
        noteFieldIndexService.deleteAllByOwner(uid)
    }


    fun createDeleteNoteFailHistory(uid: String, process: WithdrawProcess) {
        withdrawProcessHistoryService.create(
            uid = uid,
            process = process,
            isPassed = false
        )
    }

}