package com.gabinote.coffeenote.field.web.controller

import com.gabinote.coffeenote.common.aop.auth.NeedAuth
import com.gabinote.coffeenote.common.dto.slice.controller.SlicedResControllerDto
import com.gabinote.coffeenote.common.mapping.slice.SliceMapper
import com.gabinote.coffeenote.common.util.validation.page.size.PageSizeCheck
import com.gabinote.coffeenote.common.util.validation.page.sort.PageSortKeyCheck
import com.gabinote.coffeenote.field.domain.field.FieldSortKey
import com.gabinote.coffeenote.field.dto.field.controller.FieldCreateDefaultReqControllerDto
import com.gabinote.coffeenote.field.dto.field.controller.FieldResControllerDto
import com.gabinote.coffeenote.field.dto.field.controller.FieldUpdateDefaultReqControllerDto
import com.gabinote.coffeenote.field.enums.userSearch.FieldAdminSearchScope
import com.gabinote.coffeenote.field.mapping.field.FieldMapper
import com.gabinote.coffeenote.field.service.field.FieldService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@Validated
@RequestMapping("/admin/api/v1")
@RestController
class FieldAdminApiController(
    private val fieldService: FieldService,
    private val fieldMapper: FieldMapper,
    private val sliceMapper: SliceMapper,
) {
    @NeedAuth
    @GetMapping("/fields")
    fun getFields(
        @PageSizeCheck(min = 1, max = 100)
        @PageableDefault(page = 0, size = 20, sort = ["default"], direction = Sort.Direction.DESC)
        @PageSortKeyCheck(sortKey = FieldSortKey::class, message = "Invalid sort key")
        pageable: Pageable,

        @RequestParam(name = "scope", required = false, defaultValue = "ALL")
        scope: FieldAdminSearchScope
    ): ResponseEntity<SlicedResControllerDto<FieldResControllerDto>> {
        val fields = fieldService.getAllByAdminScope(pageable = pageable, scope = scope)
        val data = fields.map { fieldMapper.toResControllerDto(it) }
        val res = sliceMapper.toSlicedResponse(data)
        return ResponseEntity.ok(res)
    }

    @NeedAuth
    @GetMapping("/field/{extId}")
    fun getFieldById(
        @PathVariable extId: UUID
    ): ResponseEntity<FieldResControllerDto> {
        val field = fieldService.getByExternalId(externalId = extId)
        val data = fieldMapper.toResControllerDto(field)
        return ResponseEntity.ok(data)
    }

    @NeedAuth
    @PostMapping("/field/default")
    fun createDefaultField(
        @RequestBody @Valid dto: FieldCreateDefaultReqControllerDto,
    ): ResponseEntity<FieldResControllerDto> {
        val reqDto = fieldMapper.toCreateDefaultReqServiceDto(dto)
        val field = fieldService.createDefaultField(dto = reqDto)
        val data = fieldMapper.toResControllerDto(field)
        return ResponseEntity.status(HttpStatus.CREATED).body(data)
    }

    @NeedAuth
    @PutMapping("/field/default/{extId}")
    fun updateDefaultField(
        @PathVariable extId: UUID,
        @RequestBody @Valid dto: FieldUpdateDefaultReqControllerDto,
    ): ResponseEntity<FieldResControllerDto> {
        val reqDto = fieldMapper.toUpdateDefaultReqServiceDto(dto = dto, externalId = extId)
        val field = fieldService.updateDefaultField(dto = reqDto)
        val data = fieldMapper.toResControllerDto(field)
        return ResponseEntity.ok(data)
    }

    @NeedAuth
    @DeleteMapping("/field/default/{extId}")
    fun deleteDefaultField(
        @PathVariable extId: UUID,
    ): ResponseEntity<Void> {
        fieldService.deleteDefaultByExternalId(externalId = extId)
        return ResponseEntity.noContent().build()
    }
}