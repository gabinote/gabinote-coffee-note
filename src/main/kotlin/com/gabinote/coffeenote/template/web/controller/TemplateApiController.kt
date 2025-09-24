package com.gabinote.coffeenote.template.web.controller

import com.gabinote.coffeenote.common.aop.auth.NeedAuth
import com.gabinote.coffeenote.common.dto.slice.controller.SlicedResControllerDto
import com.gabinote.coffeenote.common.mapping.slice.SliceMapper
import com.gabinote.coffeenote.common.util.context.UserContext
import com.gabinote.coffeenote.common.util.controller.ResponseEntityHelper
import com.gabinote.coffeenote.common.util.validation.page.size.PageSizeCheck
import com.gabinote.coffeenote.common.util.validation.page.sort.PageSortKeyCheck
import com.gabinote.coffeenote.template.domain.template.TemplateSortKey
import com.gabinote.coffeenote.template.dto.template.controller.TemplateCreateReqControllerDto
import com.gabinote.coffeenote.template.dto.template.controller.TemplateResControllerDto
import com.gabinote.coffeenote.template.dto.template.controller.TemplateUpdateReqControllerDto
import com.gabinote.coffeenote.template.mapping.template.TemplateMapper
import com.gabinote.coffeenote.template.service.template.TemplateService
import com.gabinote.coffeenote.template.service.template.strategy.GetTemplateByExternalIdStrategyType
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@Validated
@RequestMapping("/api/v1")
@RestController
class TemplateApiController(
    private val templateService: TemplateService,
    private val templateMapper: TemplateMapper,
    private val userContext: UserContext,
    private val sliceMapper: SliceMapper,
) {

    @NeedAuth
    @GetMapping("/template/default/{externalId}")
    fun getDefaultTemplate(
        @PathVariable externalId: UUID
    ): ResponseEntity<TemplateResControllerDto> {
        val data = templateService.getByExternalId(
            externalId = externalId,
            requestor = userContext.uid,
            strategyType = GetTemplateByExternalIdStrategyType.DEFAULT
        )

        val res = templateMapper.toResControllerDto(data)

        return ResponseEntity.ok(res)
    }

    @NeedAuth
    @GetMapping("/template/me/{externalId}")
    fun getMyTemplate(
        @PathVariable externalId: UUID
    ): ResponseEntity<TemplateResControllerDto> {
        val data = templateService.getByExternalId(
            externalId = externalId,
            requestor = userContext.uid,
            strategyType = GetTemplateByExternalIdStrategyType.OWNED
        )
        val res = templateMapper.toResControllerDto(data)
        return ResponseEntity.ok(res)
    }

    @NeedAuth
    @GetMapping("/template/open/{externalId}")
    fun getOpenTemplate(
        @PathVariable externalId: UUID
    ): ResponseEntity<TemplateResControllerDto> {
        val data = templateService.getByExternalId(
            externalId = externalId,
            requestor = userContext.uid,
            strategyType = GetTemplateByExternalIdStrategyType.OPENED
        )
        val res = templateMapper.toResControllerDto(data)
        return ResponseEntity.ok(res)
    }

    @NeedAuth
    @GetMapping("/templates/default")
    fun getAllDefaultTemplates(
        @PageSizeCheck(min = 1, max = 100)
        @PageableDefault(page = 0, size = 20, sort = ["name"], direction = Sort.Direction.DESC)
        @PageSortKeyCheck(sortKey = TemplateSortKey::class, message = "Invalid sort key")
        pageable: Pageable,
    ): ResponseEntity<SlicedResControllerDto<TemplateResControllerDto>> {
        val templates = templateService.getAllDefault(pageable = pageable)
        val data = templates.map { templateMapper.toResControllerDto(it) }
        val res = sliceMapper.toSlicedResponse(data)
        return ResponseEntity.ok(res)
    }

    @NeedAuth
    @GetMapping("/templates/me")
    fun getAllMyTemplates(
        @PageSizeCheck(min = 1, max = 100)
        @PageableDefault(page = 0, size = 20, sort = ["name"], direction = Sort.Direction.DESC)
        @PageSortKeyCheck(sortKey = TemplateSortKey::class, message = "Invalid sort key")
        pageable: Pageable,
    ): ResponseEntity<SlicedResControllerDto<TemplateResControllerDto>> {
        val templates = templateService.getAllOwned(pageable = pageable, owner = userContext.uid)
        val data = templates.map { templateMapper.toResControllerDto(it) }
        val res = sliceMapper.toSlicedResponse(data)
        return ResponseEntity.ok(res)
    }

    @NeedAuth
    @PostMapping("/template/me")
    fun createMyTemplate(
        @RequestBody
        @Valid
        dto: TemplateCreateReqControllerDto
    ): ResponseEntity<TemplateResControllerDto> {
        val reqDto = templateMapper.toCreateReqServiceDto(dto = dto, owner = userContext.uid)
        val template = templateService.createOwned(reqDto)
        val data = templateMapper.toResControllerDto(template)
        return ResponseEntityHelper.created(data)
    }

    @NeedAuth
    @PutMapping("/template/me/{externalId}")
    fun updateMyTemplate(
        @PathVariable externalId: UUID,
        @RequestBody
        @Valid
        dto: TemplateUpdateReqControllerDto
    ): ResponseEntity<TemplateResControllerDto> {
        val reqDto = templateMapper.toUpdateReqServiceDto(dto = dto, owner = userContext.uid, externalId = externalId)
        val template = templateService.updateOwned(dto = reqDto)
        val data = templateMapper.toResControllerDto(template)
        return ResponseEntity.ok(data)
    }

    @NeedAuth
    @DeleteMapping("/template/me/{externalId}")
    fun deleteMyTemplate(
        @PathVariable externalId: UUID
    ): ResponseEntity<Void> {
        templateService.deleteOwned(
            externalId = externalId,
            owner = userContext.uid
        )
        return ResponseEntityHelper.noContent()
    }


}