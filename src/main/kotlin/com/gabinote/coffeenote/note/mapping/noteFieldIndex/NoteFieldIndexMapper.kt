package com.gabinote.coffeenote.note.mapping.noteFieldIndex

import com.gabinote.coffeenote.common.util.meiliSearch.helper.data.FacetWithCount
import com.gabinote.coffeenote.note.dto.noteFieldIndex.service.NoteFieldNameFacetWithCountResServiceDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.service.NoteFieldValueFacetWithCountResServiceDto
import org.mapstruct.Mapper

@Mapper(
    componentModel = "spring",
)
interface NoteFieldIndexMapper {
    fun toNoteFieldNameFacetWithCountResServiceDto(facetWithCount: FacetWithCount): NoteFieldNameFacetWithCountResServiceDto
    fun toNoteFieldValueFacetWithCountResServiceDto(facetWithCount: FacetWithCount): NoteFieldValueFacetWithCountResServiceDto
}