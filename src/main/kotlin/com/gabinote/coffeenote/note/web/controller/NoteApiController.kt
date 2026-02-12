package com.gabinote.coffeenote.note.web.controller

import com.gabinote.coffeenote.common.aop.auth.NeedAuth
import com.gabinote.coffeenote.common.dto.slice.controller.SlicedResControllerDto
import com.gabinote.coffeenote.common.mapping.slice.SliceMapper
import com.gabinote.coffeenote.common.util.context.UserContext
import com.gabinote.coffeenote.common.util.controller.ResponseEntityHelper
import com.gabinote.coffeenote.common.util.validation.page.size.PageSizeCheck
import com.gabinote.coffeenote.common.util.validation.page.sort.PageSortKeyCheck
import com.gabinote.coffeenote.note.domain.note.NoteSortKey
import com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto
import com.gabinote.coffeenote.note.dto.note.controller.NoteListResControllerDto
import com.gabinote.coffeenote.note.dto.note.controller.NoteResControllerDto
import com.gabinote.coffeenote.note.dto.note.controller.NoteUpdateReqControllerDto
import com.gabinote.coffeenote.note.dto.noteField.constraint.NoteFieldConstraints
import com.gabinote.coffeenote.note.dto.noteFieldIndex.constraint.NoteFieldIndexConstraints
import com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.AllNoteFieldValueFacetListResControllerDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldNameFacetListResControllerDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldValueFacetListResControllerDto
import com.gabinote.coffeenote.note.dto.noteIndex.controller.NoteIndexResControllerDto
import com.gabinote.coffeenote.note.dto.noteIndex.controller.OwnedNoteFilterCondition
import com.gabinote.coffeenote.note.dto.noteIndex.controller.OwnedSearchNoteCondition
import com.gabinote.coffeenote.note.mapping.note.NoteMapper
import com.gabinote.coffeenote.note.mapping.noteFieldIndex.NoteFieldIndexMapper
import com.gabinote.coffeenote.note.mapping.noteIndex.NoteIndexMapper
import com.gabinote.coffeenote.note.service.note.NoteService
import com.gabinote.coffeenote.note.service.noteFieldIndex.NoteFieldIndexService
import com.gabinote.coffeenote.note.service.noteIndex.NoteIndexService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length
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
class NoteApiController(
    private val noteService: NoteService,
    private val noteFieldIndexService: NoteFieldIndexService,
    private val noteMapper: NoteMapper,
    private val sliceMapper: SliceMapper,
    private val userContext: UserContext,
    private val noteFieldIndexMapper: NoteFieldIndexMapper,
    private val noteIndexService: NoteIndexService,
    private val noteIndexMapper: NoteIndexMapper,
) {

    //me
    @NeedAuth
    @GetMapping("/notes/me")
    fun getAllMyNotes(
        @PageSizeCheck(min = 1, max = 100)
        @PageableDefault(page = 0, size = 20, sort = ["createdDate"], direction = Sort.Direction.DESC)
        @PageSortKeyCheck(sortKey = NoteSortKey::class, message = "Invalid sort key")
        pageable: Pageable,
    ): ResponseEntity<SlicedResControllerDto<NoteListResControllerDto>> {
        val notes = noteService.getAllByOwner(
            owner = userContext.uid,
            pageable = pageable
        )
        val data = notes.map { noteMapper.toListResControllerDto(it) }
        val res = sliceMapper.toSlicedResponse(data)
        return ResponseEntity.ok(res)

    }

    @NeedAuth
    @GetMapping("/note/me/{externalId}")
    fun getMyNoteByExternalId(@PathVariable externalId: UUID): ResponseEntity<NoteResControllerDto> {
        val note = noteService.getOwnedByExternalId(
            externalId = externalId,
            requestor = userContext.uid
        )
        val res = noteMapper.toResControllerDto(note)
        return ResponseEntity.ok(res)
    }

    @NeedAuth
    @GetMapping("/notes/me/search")
    fun searchMyNotes(
        @PageSizeCheck(min = 1, max = 100)
        @PageableDefault(page = 0, size = 20, sort = ["createdDate"], direction = Sort.Direction.DESC)
        @PageSortKeyCheck(sortKey = NoteSortKey::class, message = "Invalid sort key")
        pageable: Pageable,

        @Valid
        req: OwnedSearchNoteCondition,
    ): ResponseEntity<SlicedResControllerDto<NoteIndexResControllerDto>> {

        val searchCondition = noteIndexMapper.toNoteSearchCondition(
            condition = req,
            owner = userContext.uid,
            pageable = pageable,
        )

        val notes = noteIndexService.searchByCondition(
            searchCondition = searchCondition,
        )
        val data = notes.map { noteIndexMapper.toResControllerDto(it) }
        val res = sliceMapper.toSlicedResponse(data)
        return ResponseEntity.ok(res)

    }


    @NeedAuth
    @GetMapping("/notes/me/filter")
    fun filterMyNotes(
        @PageSizeCheck(min = 1, max = 100)
        @PageableDefault(page = 0, size = 20, sort = ["createdDate"], direction = Sort.Direction.DESC)
        @PageSortKeyCheck(sortKey = NoteSortKey::class, message = "Invalid sort key")
        pageable: Pageable,

        @Valid
        req: OwnedNoteFilterCondition,
    ): ResponseEntity<SlicedResControllerDto<NoteIndexResControllerDto>> {

        val condition = noteIndexMapper.toNoteFilterCondition(
            condition = req,
            owner = userContext.uid,
            pageable = pageable,
        )

        val notes = noteIndexService.filterByCondition(
            condition = condition
        )
        val data = notes.map { noteIndexMapper.toResControllerDto(it) }
        val res = sliceMapper.toSlicedResponse(data)
        return ResponseEntity.ok(res)

    }

    @NeedAuth
    @GetMapping("/notes/me/facets/fields/values/search")
    fun getMyNotesAllFieldValuesFacets(

        @Pattern(
            regexp = NoteFieldIndexConstraints.SEARCH_VALUE_STRING_REGEX,
            message = "special characters are not allowed"
        )
        @Length(
            max = NoteFieldIndexConstraints.SEARCH_VALUE_MAX_LENGTH,
            message = "query must be at most ${NoteFieldIndexConstraints.SEARCH_VALUE_MAX_LENGTH} characters long"
        )
        @NotBlank(message = "query must not be blank")
        query: String,
    ): ResponseEntity<AllNoteFieldValueFacetListResControllerDto> {
        val facets = noteFieldIndexService.searchAllNoteFieldValueFacets(
            query = query,
            owner = userContext.uid
        )
        val data = facets.map { noteFieldIndexMapper.toNoteFieldValueFacetWithCountResControllerDto(it) }
        val res = noteFieldIndexMapper.toAllNoteFieldValueFacetWithCountResControllerDto(
            facets = data
        )
        return ResponseEntity.ok(res)
    }


    @NeedAuth
    @GetMapping("/notes/me/facets/fields/{fieldName}/values/search")
    fun getMyNotesFieldValuesFacets(

        @Length(
            max = NoteFieldConstraints.FIELD_NAME_MAX_LENGTH,
            message = "Key must be at most ${NoteFieldConstraints.FIELD_NAME_MAX_LENGTH} characters long"
        )
        @Pattern(
            regexp = NoteFieldConstraints.FIELD_NAME_REGEX_STRING,
            message = "Special characters are not allowed in key"
        )
        @NotBlank(message = "query must not be blank")
        @PathVariable
        fieldName: String,

        @Pattern(
            regexp = NoteFieldIndexConstraints.SEARCH_VALUE_STRING_REGEX,
            message = "special characters are not allowed"
        )
        @Length(
            max = NoteFieldIndexConstraints.SEARCH_VALUE_MAX_LENGTH,
            message = "query must be at most ${NoteFieldIndexConstraints.SEARCH_VALUE_MAX_LENGTH} characters long"
        )
        @NotBlank(message = "query must not be blank")
        query: String,
    ): ResponseEntity<NoteFieldValueFacetListResControllerDto> {
        val facets = noteFieldIndexService.searchNoteFieldValueFacets(
            query = query,
            fieldName = fieldName,
            owner = userContext.uid
        )
        val data = facets.map { noteFieldIndexMapper.toNoteFieldValueFacetWithCountResControllerDto(it) }
        val res = noteFieldIndexMapper.toNoteFieldValueListResControllerDto(facets = data, fieldName = fieldName)
        return ResponseEntity.ok(res)
    }

    @NeedAuth
    @GetMapping("/notes/me/facets/fields/search")
    fun getMyNotesFieldFacets(
        @Length(
            max = NoteFieldIndexConstraints.SEARCH_NAME_MAX_LENGTH,
            message = "Query must be at most ${NoteFieldConstraints.FIELD_NAME_MAX_LENGTH} characters long"
        )
        @Pattern(
            regexp = NoteFieldIndexConstraints.SEARCH_NAME_STRING_REGEX,
            message = "Special characters are not allowed in query"
        )
        @NotBlank(message = "Query must not be blank")
        @RequestParam(required = true)
        query: String,
    ): ResponseEntity<NoteFieldNameFacetListResControllerDto> {
        val facets = noteFieldIndexService.searchNoteFieldNameFacets(query = query, owner = userContext.uid)
        val data = facets.map { noteFieldIndexMapper.toNoteFieldNameFacetWithCountResControllerDto(it) }
        val res = noteFieldIndexMapper.toNoteFieldNameListResControllerDto(facets = data)
        return ResponseEntity.ok(res)
    }


    @NeedAuth
    @PostMapping("/note/me")
    fun createMyNote(
        @Valid @RequestBody dto: NoteCreateReqControllerDto,
    ): ResponseEntity<NoteResControllerDto> {
        val reqDto = noteMapper.toCreateReqServiceDto(dto = dto, owner = userContext.uid)
        val data = noteService.create(reqDto)
        val res = noteMapper.toResControllerDto(data)
        return ResponseEntityHelper.created(res)
    }

    @NeedAuth
    @PutMapping("/note/me/{externalId}")
    fun updateMyNoteByExternalId(
        @PathVariable externalId: UUID,
        @Valid @RequestBody dto: NoteUpdateReqControllerDto,
    ): ResponseEntity<NoteResControllerDto> {
        val reqDto = noteMapper.toUpdateReqServiceDto(
            dto = dto,
            externalId = externalId,
            owner = userContext.uid
        )

        val data = noteService.update(reqDto)
        val res = noteMapper.toResControllerDto(data)

        return ResponseEntity.ok(res)

    }

    @DeleteMapping("/note/me/{externalId}")
    fun deleteMyNoteByExternalId(@PathVariable externalId: UUID): ResponseEntity<Void> {
//        noteService.deleteByExternalId(
//            externalId = externalId,
//            owner = userContext.uid
//        )
        noteService.softDeleteByExternalId(
            externalId = externalId,
            owner = userContext.uid
        )
        return ResponseEntityHelper.noContent()
    }

    //open
    @GetMapping("/note/open/{externalId}")
    fun getOpenNoteByExternalId(@PathVariable externalId: UUID): ResponseEntity<NoteResControllerDto> {
        val note = noteService.getOpenByExternalId(externalId = externalId)
        val res = noteMapper.toResControllerDto(note)
        return ResponseEntity.ok(res)
    }
}