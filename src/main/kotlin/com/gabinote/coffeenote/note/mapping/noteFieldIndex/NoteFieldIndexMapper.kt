package com.gabinote.coffeenote.note.mapping.noteFieldIndex

import com.gabinote.coffeenote.common.util.meiliSearch.helper.data.FacetWithCount
import com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldNameFacetListResControllerDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldNameFacetWithCountResControllerDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldValueFacetListResControllerDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldValueFacetWithCountResControllerDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.service.NoteFieldNameFacetWithCountResServiceDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.service.NoteFieldValueFacetWithCountResServiceDto
import org.mapstruct.Mapper

@Mapper(
    componentModel = "spring",
)
interface NoteFieldIndexMapper {
    fun toNoteFieldNameFacetWithCountResServiceDto(facetWithCount: FacetWithCount): NoteFieldNameFacetWithCountResServiceDto
    fun toNoteFieldValueFacetWithCountResServiceDto(facetWithCount: FacetWithCount): NoteFieldValueFacetWithCountResServiceDto

    fun toNoteFieldNameFacetWithCountResControllerDto(dto: NoteFieldNameFacetWithCountResServiceDto): NoteFieldNameFacetWithCountResControllerDto
    fun toNoteFieldValueFacetWithCountResControllerDto(dto: NoteFieldValueFacetWithCountResServiceDto): NoteFieldValueFacetWithCountResControllerDto

    fun toNoteFieldNameListResControllerDto(facets: List<NoteFieldNameFacetWithCountResControllerDto>): NoteFieldNameFacetListResControllerDto =
        NoteFieldNameFacetListResControllerDto(facets = facets)

    fun toNoteFieldValueListResControllerDto(
        facets: List<NoteFieldValueFacetWithCountResControllerDto>,
        fieldName: String,
    ): NoteFieldValueFacetListResControllerDto = NoteFieldValueFacetListResControllerDto(
        fieldName = fieldName,
        facets = facets,
    )
}