package com.gabinote.coffeenote.note.service.note.strategy

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotFound
import com.gabinote.coffeenote.note.domain.note.Note
import org.springframework.stereotype.Component

@Component
class GetNoteOwnedStrategy : GetNoteByExternalIdStrategy {
    override fun validate(requestor: String, note: Note) {
        if (note.owner != requestor) {
            throw ResourceNotFound(
                name = "Owned Note",
                identifier = note.externalId.toString(),
                identifierType = "externalId"
            )
        }
    }

    override val type = GetNoteByExternalIdStrategyType.OWNED
}