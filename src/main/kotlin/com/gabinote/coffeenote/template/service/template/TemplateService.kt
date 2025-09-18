package com.gabinote.coffeenote.template.service.template

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotFound
import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapper
import com.gabinote.coffeenote.template.domain.template.Template
import com.gabinote.coffeenote.template.domain.template.TemplateRepository
import com.gabinote.coffeenote.template.dto.template.service.TemplateCreateDefaultReqServiceDto
import com.gabinote.coffeenote.template.dto.template.service.TemplateResServiceDto
import com.gabinote.coffeenote.template.mapping.template.TemplateMapper
import com.gabinote.coffeenote.template.mapping.templateField.TemplateFieldMapper
import com.gabinote.coffeenote.template.service.template.strategy.GetTemplateByExternalIdStrategyFactory
import com.gabinote.coffeenote.template.service.template.strategy.GetTemplateByExternalIdStrategyType
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import java.util.*

@Service
class TemplateService(
    private val templateRepository: TemplateRepository,
    private val templateMapper: TemplateMapper,
    private val templateFieldMapper: TemplateFieldMapper,
    private val attributeMapper: AttributeMapper,
    private val getTemplateByExternalIdStrategyFactory: GetTemplateByExternalIdStrategyFactory
) {
    //get
    fun fetchByExternalId(externalId: UUID): Template {
        return templateRepository.findByExternalId(externalId.toString()) ?: throw ResourceNotFound(
            name = "Template",
            identifierType = "externalId",
            identifier = externalId.toString()
        )
    }

    fun getByExternalId(
        externalId: UUID,
        requestor: String,
        strategyType: GetTemplateByExternalIdStrategyType
    ): TemplateResServiceDto {
        val template = fetchByExternalId(externalId)
        val strategy = getTemplateByExternalIdStrategyFactory.getStrategy(strategyType)
        strategy.validate(requestor = requestor, template = template)
        return templateMapper.toResServiceDto(template)
    }
//    fun getByExternalId(externalId: UUID): TemplateResServiceDto {
//        val template = fetchByExternalId(externalId)
//        return templateMapper.toResServiceDto(template)
//    }
//
//    fun getOpenedByExternalId(externalId: UUID): TemplateResServiceDto {
//        val template = fetchByExternalId(externalId)
//        checkIsOpen(template)
//        return templateMapper.toResServiceDto(template)
//    }
//
//    fun getOwnedByExternalId(externalId: UUID, owner: String): TemplateResServiceDto {
//        val template = fetchByExternalId(externalId)
//        checkOwnership(template, owner)
//        return templateMapper.toResServiceDto(template)
//    }
//
//    fun getDefaultByExternalId(externalId: UUID): TemplateResServiceDto {
//        val template = fetchByExternalId(externalId)
//        checkIsDefault(template)
//        return templateMapper.toResServiceDto(template)
//    }

    fun getAll(pageable: Pageable): Slice<TemplateResServiceDto> {
        val templates = templateRepository.findAll(pageable)
        return templates.map { templateMapper.toResServiceDto(it) }
    }

    fun getAllOwned(owner: String, pageable: Pageable): Slice<TemplateResServiceDto> {
        val templates = templateRepository.findAllByOwner(owner, pageable)
        return templates.map { templateMapper.toResServiceDto(it) }
    }

    fun getAllDefault(pageable: Pageable): Slice<TemplateResServiceDto> {
        val templates = templateRepository.findAllByIsDefault(pageable = pageable)
        return templates.map { templateMapper.toResServiceDto(it) }
    }

    //create
    fun createDefault(dto: TemplateCreateDefaultReqServiceDto): TemplateResServiceDto {
        val template = templateMapper.toDefaultTemplate(dto)
        val savedTemplate = templateRepository.save(template)
        return templateMapper.toResServiceDto(savedTemplate)
    }

    //update
    //delete
    private fun checkOwnership(template: Template, owner: String) {
        if (template.owner != owner) {
            throw ResourceNotFound(
                name = "Owned Template",
                identifierType = "externalId",
                identifier = template.externalId.toString()
            )
        }
    }

    private fun checkIsDefault(template: Template) {
        if (!template.isDefault) {
            throw ResourceNotFound(
                name = "Default Template",
                identifierType = "externalId",
                identifier = template.externalId.toString()
            )
        }
    }

    private fun checkIsOpen(template: Template) {
        if (!template.isOpen) {
            throw ResourceNotFound(
                name = "Opened Template",
                identifierType = "externalId",
                identifier = template.externalId.toString()
            )
        }
    }

}