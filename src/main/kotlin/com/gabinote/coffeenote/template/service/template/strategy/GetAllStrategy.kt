package com.gabinote.coffeenote.template.service.template.strategy

import com.gabinote.coffeenote.template.domain.template.Template
import org.springframework.stereotype.Component

@Component
class GetAllStrategy : GetTemplateByExternalIdStrategy {
    override fun validate(requestor: String, template: Template) {
        // No validation needed, all templates are accessible
    }

    override val type = GetTemplateByExternalIdStrategyType.ALL
}