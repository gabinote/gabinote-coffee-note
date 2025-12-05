package com.gabinote.coffeenote.note.service.noteFieldIndex

import com.gabinote.coffeenote.note.domain.noteFieldIndex.NoteFieldIndexRepository
import com.gabinote.coffeenote.note.dto.noteFieldIndex.service.NoteFieldNameFacetWithCountResServiceDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.service.NoteFieldValueFacetWithCountResServiceDto
import com.gabinote.coffeenote.note.mapping.noteFieldIndex.NoteFieldIndexMapper
import org.springframework.stereotype.Service

@Service
class NoteFieldIndexService(
    private val noteFieldIndexRepository: NoteFieldIndexRepository,
    private val noteFieldIndexMapper: NoteFieldIndexMapper,
) {
    fun searchNoteFieldNameFacets(
        owner: String,
        query: String,
    ): List<NoteFieldNameFacetWithCountResServiceDto> {
        val data = noteFieldIndexRepository.searchFieldNameFacets(
            owner = owner,
            query = query,
        )
        return data.map {
            noteFieldIndexMapper.toNoteFieldNameFacetWithCountResServiceDto(it)
        }
    }

    fun searchNoteFieldValueFacets(
        owner: String,
        fieldName: String,
        query: String,
    ): List<NoteFieldValueFacetWithCountResServiceDto> {
        val data = noteFieldIndexRepository.searchFieldValueFacets(
            owner = owner,
            fieldName = fieldName,
            query = query,
        )
        return data.map {
            noteFieldIndexMapper.toNoteFieldValueFacetWithCountResServiceDto(it)
        }
    }


}