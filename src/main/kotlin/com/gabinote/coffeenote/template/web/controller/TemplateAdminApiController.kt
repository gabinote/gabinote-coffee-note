package com.gabinote.coffeenote.template.web.controller

import com.gabinote.coffeenote.common.aop.auth.NeedAuth
import com.gabinote.coffeenote.common.util.controller.ResponseEntityHelper
import com.gabinote.coffeenote.template.dto.template.controller.TemplateCreateDefaultReqControllerDto
import com.gabinote.coffeenote.template.dto.template.controller.TemplateResControllerDto
import com.gabinote.coffeenote.template.dto.template.controller.TemplateUpdateDefaultReqControllerDto
import com.gabinote.coffeenote.template.mapping.template.TemplateMapper
import com.gabinote.coffeenote.template.service.template.TemplateService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@Validated
@RequestMapping("/admin/api/v1")
@RestController
class TemplateAdminApiController(
    private val templateService: TemplateService,
    private val templateMapper: TemplateMapper,

    ) {

    @NeedAuth
    @PostMapping("/template/default")
    fun createDefaultTemplate(
        @RequestBody
        @Valid
        dto: TemplateCreateDefaultReqControllerDto,
    ): ResponseEntity<TemplateResControllerDto> {
        val reqDto = templateMapper.toCreateDefaultReqServiceDto(dto = dto)
        val data = templateService.createDefault(dto = reqDto)
        val res = templateMapper.toResControllerDto(data)
        return ResponseEntityHelper.created(res)
    }

    @NeedAuth
    @PutMapping("/template/default/{externalId}")
    fun updateDefaultTemplate(
        @PathVariable
        externalId: UUID,
        @RequestBody
        @Valid
        dto: TemplateUpdateDefaultReqControllerDto,
    ): ResponseEntity<TemplateResControllerDto> {
        val reqDto = templateMapper.toUpdateDefaultReqServiceDto(dto = dto, externalId = externalId)
        val data = templateService.updateDefault(dto = reqDto)
        val res = templateMapper.toResControllerDto(data)
        return ResponseEntity.ok(res)
    }

    @NeedAuth
    @DeleteMapping("/template/default/{externalId}")
    fun deleteDefaultTemplate(
        @PathVariable
        externalId: UUID,
    ): ResponseEntity<Void> {
        templateService.deleteDefault(externalId = externalId)
        return ResponseEntityHelper.noContent()
    }
}