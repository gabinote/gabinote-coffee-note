package com.gabinote.coffeenote.template.service.template.strategy

import com.gabinote.coffeenote.common.util.strategy.AbstractStrategyFactory
import org.springframework.stereotype.Component

@Component
class GetTemplateByExternalIdStrategyFactory(
    strategies: List<GetTemplateByExternalIdStrategy>
) : AbstractStrategyFactory<GetTemplateByExternalIdStrategyType, GetTemplateByExternalIdStrategy>(strategies)