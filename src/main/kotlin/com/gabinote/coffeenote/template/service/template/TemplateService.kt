package com.gabinote.coffeenote.template.service.template

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotFound
import com.gabinote.coffeenote.template.domain.template.Template
import com.gabinote.coffeenote.template.domain.template.TemplateRepository
import com.gabinote.coffeenote.template.dto.template.service.*
import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldCreateReqServiceDto
import com.gabinote.coffeenote.template.mapping.template.TemplateMapper
import com.gabinote.coffeenote.template.service.template.strategy.GetTemplateByExternalIdStrategyFactory
import com.gabinote.coffeenote.template.service.template.strategy.GetTemplateByExternalIdStrategyType
import com.gabinote.coffeenote.template.service.templateField.TemplateFieldService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import java.util.*

@Service
class TemplateService(
    private val templateRepository: TemplateRepository,
    private val templateMapper: TemplateMapper,
    private val getTemplateByExternalIdStrategyFactory: GetTemplateByExternalIdStrategyFactory,
    private val templateFieldService: TemplateFieldService
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

    fun getAll(pageable: Pageable): Slice<TemplateResServiceDto> {
        val templates = templateRepository.findAllBy(pageable)
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
        return createTemplate(dto.fields, template)
    }

    fun createOwned(dto: TemplateCreateReqServiceDto): TemplateResServiceDto {
        //TODO 사용자 갯수 제한
        val template = templateMapper.toTemplate(dto)
        return createTemplate(dto.fields, template)
    }


    //update
    // TODO 중복 분리하기
    fun updateDefault(dto: TemplateUpdateDefaultReqServiceDto): TemplateResServiceDto {
        val existsTemplate = fetchByExternalId(dto.externalId)
        checkIsDefault(existsTemplate)
        templateMapper.updateDefaultFromDto(dto = dto, entity = existsTemplate)
        val updatedFields = templateFieldService.create(dto.fields)
        existsTemplate.changeFields(updatedFields)

        val savedTemplate = templateRepository.save(existsTemplate)
        return templateMapper.toResServiceDto(savedTemplate)
    }

    fun updateOwned(dto: TemplateUpdateReqServiceDto): TemplateResServiceDto {
        val existsTemplate = fetchByExternalId(dto.externalId)
        checkOwnership(existsTemplate, dto.owner)
        templateMapper.updateFromDto(dto = dto, entity = existsTemplate)
        val updatedFields = templateFieldService.create(dto.fields)
        existsTemplate.changeFields(updatedFields)

        val savedTemplate = templateRepository.save(existsTemplate)
        return templateMapper.toResServiceDto(savedTemplate)
    }


    //delete
    fun deleteOwned(externalId: UUID, owner: String) {
        val existsTemplate = fetchByExternalId(externalId)
        checkOwnership(existsTemplate, owner)
        templateRepository.delete(existsTemplate)
    }

    fun deleteDefault(externalId: UUID) {
        val existsTemplate = fetchByExternalId(externalId)
        checkIsDefault(existsTemplate)
        templateRepository.delete(existsTemplate)
    }

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

    private fun createTemplate(
        templateFields: List<TemplateFieldCreateReqServiceDto>,
        template: Template
    ): TemplateResServiceDto {
        val templateFields = templateFieldService.create(templateFields)
        template.changeFields(templateFields)

        val savedTemplate = templateRepository.save(template)
        return templateMapper.toResServiceDto(savedTemplate)
    }
}

