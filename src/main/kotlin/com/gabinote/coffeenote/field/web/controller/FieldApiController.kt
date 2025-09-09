package com.gabinote.coffeenote.field.web.controller

import com.gabinote.coffeenote.common.aop.auth.NeedAuth
import com.gabinote.coffeenote.common.dto.slice.controller.SlicedResControllerDto
import com.gabinote.coffeenote.common.mapping.slice.SliceMapper
import com.gabinote.coffeenote.common.util.context.UserContext
import com.gabinote.coffeenote.common.util.validation.page.size.PageSizeCheck
import com.gabinote.coffeenote.common.util.validation.page.sort.PageSortKeyCheck
import com.gabinote.coffeenote.field.domain.field.FieldSortKey
import com.gabinote.coffeenote.field.dto.field.controller.FieldCreateReqControllerDto
import com.gabinote.coffeenote.field.dto.field.controller.FieldResControllerDto
import com.gabinote.coffeenote.field.dto.field.controller.FieldUpdateReqControllerDto
import com.gabinote.coffeenote.field.enums.userSearch.FieldUserSearchScope
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
@RequestMapping("/api/v1")
@RestController
class FieldApiController(
    private val fieldService: FieldService,
    private val fieldMapper: FieldMapper,
    private val sliceMapper: SliceMapper,
    private val userContext: UserContext
) {

    @NeedAuth
    @GetMapping("/fields")
    fun getDefaultOrOwnedFields(
        @PageSizeCheck(min = 1, max = 100)
        @PageableDefault(page = 0, size = 20, sort = ["isDefault"], direction = Sort.Direction.DESC)
        @PageSortKeyCheck(sortKey = FieldSortKey::class, message = "Invalid sort key")
        pageable: Pageable,

        @RequestParam(name = "scope", required = false, defaultValue = "ALL")
        scope: FieldUserSearchScope
    ): ResponseEntity<SlicedResControllerDto<FieldResControllerDto>> {
        val fields = fieldService.getAllByUserScope(pageable = pageable, executor = userContext.uid, scope = scope)
        val data = fields.map { fieldMapper.toResControllerDto(it) }
        val res = sliceMapper.toSlicedResponse(data)
        return ResponseEntity.ok(res)
    }

    @NeedAuth
    @GetMapping("/field/me/{extId}")
    fun getOwnedFieldById(
        @PathVariable extId: UUID
    ): ResponseEntity<FieldResControllerDto> {
        val field = fieldService.getOwnedByExternalId(externalId = extId, executor = userContext.uid)
        val data = fieldMapper.toResControllerDto(field)
        return ResponseEntity.ok(data)
    }

    @GetMapping("/field/default/{extId}")
    fun getDefaultFieldById(
        @PathVariable extId: UUID
    ): ResponseEntity<FieldResControllerDto> {
        val field = fieldService.getDefaultByExternalId(externalId = extId)
        val data = fieldMapper.toResControllerDto(field)
        return ResponseEntity.ok(data)
    }

    @NeedAuth
    @PostMapping("/field/me")
    fun createOwnedField(
        @Valid @RequestBody dto: FieldCreateReqControllerDto
    ): ResponseEntity<FieldResControllerDto> {
        val reqDto = fieldMapper.toCreateReqServiceDto(dto = dto, owner = userContext.uid)
        val field = fieldService.createOwnedField(reqDto)
        val data = fieldMapper.toResControllerDto(field)
        return ResponseEntity.status(HttpStatus.CREATED).body(data)
    }

    @NeedAuth
    @PutMapping("/field/me/{extId}")
    fun updateOwnedField(
        @PathVariable extId: UUID,
        @Valid @RequestBody dto: FieldUpdateReqControllerDto
    ): ResponseEntity<FieldResControllerDto> {
        val reqDto = fieldMapper.toUpdateReqServiceDto(dto = dto, owner = userContext.uid, externalId = extId)
        val field = fieldService.updateOwnedField(dto = reqDto)
        val data = fieldMapper.toResControllerDto(dto = field)
        return ResponseEntity.ok(data)
    }

    @NeedAuth
    @DeleteMapping("/field/me/{extId}")
    fun deleteOwnedField(
        @PathVariable extId: UUID
    ): ResponseEntity<Void> {
        fieldService.deleteOwnedByExternalId(externalId = extId, executor = userContext.uid)
        return ResponseEntity.noContent().build()
    }

}