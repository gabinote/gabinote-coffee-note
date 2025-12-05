package com.gabinote.coffeenote.note.service.note.strategy

import com.gabinote.coffeenote.common.util.strategy.Strategy
import com.gabinote.coffeenote.note.domain.note.Note

interface GetNoteByExternalIdStrategy : Strategy<GetNoteByExternalIdStrategyType> {
    fun validate(requestor: String, note: Note)
    override val type: GetNoteByExternalIdStrategyType
}