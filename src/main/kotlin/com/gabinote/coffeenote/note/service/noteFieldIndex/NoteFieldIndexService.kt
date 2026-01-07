package com.gabinote.coffeenote.note.service.noteFieldIndex

import com.gabinote.coffeenote.common.util.time.TimeProvider
import com.gabinote.coffeenote.common.util.uuid.UuidSource
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeFactory
import com.gabinote.coffeenote.note.domain.note.Note
import com.gabinote.coffeenote.note.domain.note.NoteField
import com.gabinote.coffeenote.note.domain.noteFieldIndex.NoteFieldIndex
import com.gabinote.coffeenote.note.domain.noteFieldIndex.NoteFieldIndexRepository
import com.gabinote.coffeenote.note.dto.noteFieldIndex.service.NoteFieldNameFacetWithCountResServiceDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.service.NoteFieldValueFacetWithCountResServiceDto
import com.gabinote.coffeenote.note.mapping.noteFieldIndex.NoteFieldIndexMapper
import org.springframework.stereotype.Service
import java.util.*


@Service
class NoteFieldIndexService(
    private val noteFieldIndexRepository: NoteFieldIndexRepository,
    private val noteFieldIndexMapper: NoteFieldIndexMapper,
    private val uuidSource: UuidSource,
    private val fieldTypeFactory: FieldTypeFactory,
    private val timeProvider: TimeProvider,
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

    fun createFromNote(note: Note) {
        val noteIndex = convertToNoteFieldIndex(note)
        noteFieldIndexRepository.saveAll(noteIndex)
    }


    fun deleteByNoteExtId(noteId: UUID) {
        noteFieldIndexRepository.deleteAllByNoteId(noteId.toString())
    }

    fun deleteAllByOwner(owner: String) {
        noteFieldIndexRepository.deleteAllByOwner(owner)
    }

    private fun convertToNoteFieldIndex(note: Note): List<NoteFieldIndex> {
        val fields = note.fields
        val indexes = mutableListOf<NoteFieldIndex>()
        fields.forEach {
            val fieldIndexes = convertToNoteFieldIndexPerField(
                noteField = it,
                noteExtId = note.externalId!!,
                owner = note.owner,
                noteHash = note.hash!!,
            )
            indexes.addAll(fieldIndexes)
        }

        return indexes
    }

    private fun convertToNoteFieldIndexPerField(
        noteField: NoteField,
        noteExtId: String,
        noteHash: String,
        owner: String,

        ): List<NoteFieldIndex> {
        if (isExcludeIndexingFieldType(noteField.type)) {
            return emptyList()
        }
        val res = mutableListOf<NoteFieldIndex>()

        noteField.values.forEach {
            val noteFieldIndex = convertToNoteFieldIndexPerValue(
                noteField = noteField,
                noteExtId = noteExtId,
                owner = owner,
                value = it,
                noteHash = noteHash,

                )
            res.add(noteFieldIndex)
        }

        return res
    }

    private fun convertToNoteFieldIndexPerValue(
        noteExtId: String,
        noteField: NoteField,
        noteHash: String,
        value: String,
        owner: String,
    ): NoteFieldIndex {
        val offset = timeProvider.zoneOffset()
        val noteFieldIndex = NoteFieldIndex(
            id = uuidSource.generateUuid().toString(),
            noteId = noteExtId,
            name = noteField.name,
            value = value,
            owner = owner,
            synchronizedAt = timeProvider.now().toEpochSecond(offset),
            noteHash = noteHash
        )
        return noteFieldIndex
    }

    private fun isExcludeIndexingFieldType(fieldTypeKey: String): Boolean {
        val fieldType = fieldTypeFactory.getFieldType(fieldTypeKey)
            ?: throw IllegalArgumentException("Invalid field type key: $fieldTypeKey")
        return fieldType.isExcludeIndexing
    }
}