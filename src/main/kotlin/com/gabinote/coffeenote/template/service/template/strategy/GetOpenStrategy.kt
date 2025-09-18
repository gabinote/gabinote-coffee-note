package com.gabinote.coffeenote.template.service.template.strategy

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotFound
import com.gabinote.coffeenote.template.domain.template.Template
import org.springframework.stereotype.Component

@Component
class GetOpenStrategy : GetTemplateByExternalIdStrategy {
    override fun validate(requestor: String, template: Template) {
        if (!template.isOpen) {
            throw ResourceNotFound(
                name = "Opened Template",
                identifier = template.externalId.toString(),
                identifierType = "externalId"
            )
        }
    }

    override val type = GetTemplateByExternalIdStrategyType.OPENED
}