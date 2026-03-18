package com.gabinote.coffeenote.note.event.userWithdraw

import com.gabinote.coffeenote.note.service.note.NoteService
import com.gabinote.coffeenote.user.event.userWithdraw.UserWithdrawEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component


private val logger = KotlinLogging.logger {}

@Component
class NoteUserWithdrawEventListener(
    private val noteService: NoteService,
) {

    @EventListener
    fun handleUserWithdrawEvent(event: UserWithdrawEvent) {
        logger.info { "Trying Delete Withdraw User's notes UID=${event.uid}" }
        runCatching {
            noteService.deleteAllByOwner(event.uid.toString())
        }.onSuccess {
            logger.info { "Successfully Deleted Withdraw User's notes UID=${event.uid}" }
        }.onFailure {
            logger.error(it) { "Failed to Delete Withdraw User's notes UID=${event.uid}" }
        }
    }
}