package com.gabinote.coffeenote.note.service.note.strategy

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotFound
import com.gabinote.coffeenote.note.domain.note.Note
import org.springframework.stereotype.Component

@Component
class GetOpenStrategy : GetNoteByExternalIdStrategy {
    override fun validate(requestor: String, note: Note): Note {
        if (!note.isOpen) {
            throw ResourceNotFound(
                name = "Opened Note",
                identifier = note.externalId.toString(),
                identifierType = "externalId"
            )
        }
        return note
    }

    override val type = GetNoteByExternalIdStrategyType.OPENED
}