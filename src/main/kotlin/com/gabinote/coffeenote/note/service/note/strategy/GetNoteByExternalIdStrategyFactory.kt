package com.gabinote.coffeenote.note.service.note.strategy

import com.gabinote.coffeenote.common.util.strategy.AbstractStrategyFactory
import org.springframework.stereotype.Component

@Component
class GetNoteByExternalIdStrategyFactory(
    strategies: List<GetNoteByExternalIdStrategy>,
) : AbstractStrategyFactory<GetNoteByExternalIdStrategyType, GetNoteByExternalIdStrategy>(strategies)