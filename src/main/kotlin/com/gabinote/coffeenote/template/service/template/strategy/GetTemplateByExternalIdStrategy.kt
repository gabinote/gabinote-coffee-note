package com.gabinote.coffeenote.template.service.template.strategy

import com.gabinote.coffeenote.common.util.strategy.Strategy
import com.gabinote.coffeenote.template.domain.template.Template

interface GetTemplateByExternalIdStrategy : Strategy<GetTemplateByExternalIdStrategyType> {
    fun validate(requestor: String, template: Template)
    override val type: GetTemplateByExternalIdStrategyType
}