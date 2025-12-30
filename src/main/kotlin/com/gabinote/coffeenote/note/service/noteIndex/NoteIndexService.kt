package com.gabinote.coffeenote.note.service.noteIndex

import com.gabinote.coffeenote.common.util.time.TimeProvider
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeFactory
import com.gabinote.coffeenote.note.domain.note.Note
import com.gabinote.coffeenote.note.domain.note.NoteDisplayField
import com.gabinote.coffeenote.note.domain.note.NoteField
import com.gabinote.coffeenote.note.domain.noteIndex.IndexDisplayField
import com.gabinote.coffeenote.note.domain.noteIndex.NoteIndex
import com.gabinote.coffeenote.note.domain.noteIndex.NoteIndexRepository
import com.gabinote.coffeenote.note.domain.noteIndex.vo.DateRangeFilter
import com.gabinote.coffeenote.note.dto.noteIndex.domain.NoteFilterCondition
import com.gabinote.coffeenote.note.dto.noteIndex.domain.NoteSearchCondition
import com.gabinote.coffeenote.note.dto.noteIndex.service.NoteIndexResServiceDto
import com.gabinote.coffeenote.note.mapping.noteIndex.NoteIndexMapper
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class NoteIndexService(
    private val noteIndexRepository: NoteIndexRepository,
    private val noteIndexMapper: NoteIndexMapper,
    private val timeProvider: TimeProvider,
    private val fieldTypeFactory: FieldTypeFactory,
) {
    fun searchByCondition(
        searchCondition: NoteSearchCondition,
    ): Slice<NoteIndexResServiceDto> {
        val indexes = noteIndexRepository.searchNotes(
            owner = searchCondition.owner,
            query = searchCondition.query,
            pageable = searchCondition.pageable,
            highlightTag = searchCondition.highlightTag

        )
        return indexes.map {
            noteIndexMapper.toResServiceDto(it)
        }
    }

    fun filterByCondition(
        condition: NoteFilterCondition,
    ): Slice<NoteIndexResServiceDto> {
        val offset = timeProvider.zoneOffset()
        val indexes = noteIndexRepository.searchNotesWithFilter(
            owner = condition.owner,
            filters = condition.fieldOptions,
            pageable = condition.pageable,
            highlightTag = condition.highlightTag,
            createdDateFilter = DateRangeFilter(
                startDate = condition.createdDateStart?.toEpochSecond(offset),
                endDate = condition.createdDateEnd?.toEpochSecond(offset),
            ),
            modifiedDateFilter = DateRangeFilter(
                startDate = condition.modifiedDateStart?.toEpochSecond(offset),
                endDate = condition.modifiedDateEnd?.toEpochSecond(offset),
            ),
        )

        return indexes.map {
            noteIndexMapper.toResServiceDto(it)
        }

    }


    fun createFromNote(note: Note) {
        val noteIndex = convertToNoteIndex(note)
        noteIndexRepository.save(noteIndex)
    }

    private fun convertToNoteIndex(note: Note): NoteIndex {
        val displayFields = convertToDisplayFields(note.displayFields)
        val filters = convertToFilters(note.fields)

        val offset = timeProvider.zoneOffset()
        return NoteIndex(
            id = note.id!!.toString(),
            externalId = note.externalId!!,
            title = note.title,
            owner = note.owner,
            createdDate = note.createdDate!!.toEpochSecond(offset),
            modifiedDate = note.modifiedDate!!.toEpochSecond(offset),
            displayFields = displayFields,
            filters = filters,
            synchronizedAt = timeProvider.now().toEpochSecond(offset),
        )
    }

    private fun convertToFilters(noteFields: List<NoteField>): Map<String, List<String>> {
        val filters = mutableMapOf<String, List<String>>()
        noteFields.forEach { field ->
            if (isExcludeIndexingFieldType(field.type)) {
                return@forEach
            }
            filters[field.name] = field.values.toList()
        }
        return filters
    }

    private fun convertToDisplayFields(noteFields: List<NoteDisplayField>): List<IndexDisplayField> {
        return noteFields.map {
            convertToDisplayFieldsPerField(it)
        }
    }

    private fun convertToDisplayFieldsPerField(noteField: NoteDisplayField): IndexDisplayField {
        return IndexDisplayField(
            name = noteField.name,
            value = noteField.values.toList(),
            tag = noteField.icon,
            order = noteField.order,
        )
    }

    private fun isExcludeIndexingFieldType(fieldTypeKey: String): Boolean {
        val fieldType = fieldTypeFactory.getFieldType(fieldTypeKey)!!
        return fieldType.isExcludeIndexing
    }
}