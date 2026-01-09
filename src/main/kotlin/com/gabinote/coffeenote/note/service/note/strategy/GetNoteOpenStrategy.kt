package com.gabinote.coffeenote.note.service.note.strategy

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotFound
import com.gabinote.coffeenote.note.domain.note.Note
import com.gabinote.coffeenote.note.domain.note.NoteStatus
import org.springframework.stereotype.Component

@Component
class GetNoteOpenStrategy : GetNoteByExternalIdStrategy {
    override fun validate(requestor: String, note: Note) {
        if (!note.isOpen) {
            throw ResourceNotFound(
                name = "Opened Note",
                identifier = note.externalId.toString(),
                identifierType = "externalId"
            )
        }

        if (note.status != NoteStatus.ACTIVE) {
            throw ResourceNotFound(
                name = "Opened Note",
                identifier = note.externalId.toString(),
                identifierType = "externalId"
            )
        }
    }

    override val type = GetNoteByExternalIdStrategyType.OPENED
}