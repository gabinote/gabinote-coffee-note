package com.gabinote.coffeenote.note.service.noteSync

import com.gabinote.coffeenote.note.domain.note.NoteStatus
import com.gabinote.coffeenote.note.dto.note.vo.NoteOnlyField
import com.gabinote.coffeenote.note.dto.noteFieldIndex.vo.NoteFieldIndexNoteIdHash
import com.gabinote.coffeenote.note.service.note.NoteService
import com.gabinote.coffeenote.note.service.noteFieldIndex.NoteFieldIndexService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class NoteFieldIndexSyncService(
    noteService: NoteService,
    private val noteFieldIndexService: NoteFieldIndexService,
) : NoteSyncService<NoteOnlyField, NoteFieldIndexStatus>(noteService) {

    override fun compareWithNoteIndex(notes: List<NoteOnlyField>): Map<NoteFieldIndexStatus, List<String>> {
        val noteMap = convertNoteOnlyFieldToMap(notes)
        val fieldIndexes = noteFieldIndexService.getAllByNoteIds(noteMap.keys.toList())
        val fieldIndexMap = convertNoteFieldIndexNoteIdHashToMap(fieldIndexes)

        val res = createIncorrectIndexMap()
        noteMap.forEach { (noteId, note) ->
            val indexes = fieldIndexMap[noteId]

            when {
                isIndexNotCreated(note, indexes) -> {
                    logger.debug { "noteId $noteId has no index entries." }
                    res[NoteFieldIndexStatus.NOT_CREATED]!!.add(noteId)
                }

                isIndexNotDeleted(note, indexes) -> {
                    logger.debug { "noteId $noteId has not-deleted index entries." }
                    res[NoteFieldIndexStatus.NOT_DELETED]!!.add(noteId)
                }

                isIndexDifferent(note, indexes) -> {
                    logger.debug { "noteId $noteId has different index entries." }
                    res[NoteFieldIndexStatus.INVALID]!!.add(noteId)
                }
            }
        }

        return res
    }

    override fun recoverIncorrectIndexes(incorrectIndexMap: Map<NoteFieldIndexStatus, List<String>>) {
        incorrectIndexMap[NoteFieldIndexStatus.INVALID]?.let { noteId ->
            noteId.forEach {
                processInvalidIndexes(it)
            }
        }

        incorrectIndexMap[NoteFieldIndexStatus.NOT_CREATED]?.let { noteId ->
            noteId.forEach {
                processNotCreatedIndexes(it)
            }

        }

        incorrectIndexMap[NoteFieldIndexStatus.NOT_DELETED]?.let { noteId ->
            noteId.forEach {
                processNotDeletedIndexes(it)
            }
        }
    }

    override fun fetchCurrNote(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        pageable: Pageable,
    ): List<NoteOnlyField> = noteService.getAllNoteFieldsWithBetweenModifiedDate(startDate, endDate, pageable)

    override fun fetchAllNote(
        beforeDate: LocalDateTime,
        pageable: Pageable,
    ): List<NoteOnlyField> = noteService.getAllNoteFieldsBeforeModifiedDate(beforeDate, pageable)

    private fun convertNoteOnlyFieldToMap(
        notes: List<NoteOnlyField>,
    ): Map<String, NoteOnlyField> {
        return notes.associateBy { it.externalId }
    }

    private fun convertNoteFieldIndexNoteIdHashToMap(
        fieldIndexes: List<NoteFieldIndexNoteIdHash>,
    ): Map<String, List<NoteFieldIndexNoteIdHash>> {
        return fieldIndexes.groupBy { it.noteId }
    }

    private fun isIndexNotCreated(
        note: NoteOnlyField,
        index: List<NoteFieldIndexNoteIdHash>?,
    ): Boolean {
        // note 가 삭제된 상태가 아니고, index 가 없는 경우
        return note.status != NoteStatus.DELETED && index.isNullOrEmpty()
    }

    // 실제 Note와 필드 인덱스가 다른지 비교
    private fun isIndexDifferent(
        note: NoteOnlyField,
        index: List<NoteFieldIndexNoteIdHash>?,
    ): Boolean {
        if (note.status == NoteStatus.DELETED) {
            return false
        }
        //1. 필드 개수 비교
        if (note.fields.size != index!!.size) {
            return true
        }

        //2. 필드 실제 값 비교
        note.fields.forEach { field ->
            val indexForField = index.filter { it.fieldId == field.id }

            //2-1. 실제 필드 값과 인덱스의 필드 값이 동일해야함.
            val indexFieldValues = indexForField.map { it.value }.toSet()
            if (indexFieldValues != field.values) {
                return true
            }

            //2-2. 모든 field 이름이 동일해야함.
            val indexFieldNames = indexForField.map { it.name }.toSet()
            // 필드 이름은 1개이므로 2개 이상이 될 수 없음.
            if (indexFieldNames.size != 1 || indexFieldNames.first() != field.name) {
                return true
            }
        }
        return false
    }

    private fun isIndexNotDeleted(
        note: NoteOnlyField,
        index: List<NoteFieldIndexNoteIdHash>?,
    ): Boolean {
        // note 가 삭제된 상태이고, index 가 존재하는 경우
        return note.status == NoteStatus.DELETED && !index.isNullOrEmpty()
    }


    private fun createIncorrectIndexMap(): MutableMap<NoteFieldIndexStatus, MutableList<String>> {
        val res: MutableMap<NoteFieldIndexStatus, MutableList<String>> = mutableMapOf()
        NoteFieldIndexStatus.entries.forEach {
            res[it] = mutableListOf()
        }
        return res
    }

    // TODO : 추후에 한번에 필터로 삭제하는 방식으로 변경
    private fun processNotDeletedIndexes(noteId: String) {
        runCatching {
            noteFieldIndexService.deleteByNoteExtId(UUID.fromString(noteId))
        }.onFailure { ex ->
            logger.error(ex) { "Failed to delete not-deleted indexes for noteIds: $noteId" }
        }
    }

    private fun processInvalidIndexes(noteId: String) {
        runCatching {
            val id = UUID.fromString(noteId)
            noteFieldIndexService.deleteByNoteExtId(id)
            val note = noteService.fetchByExternalId(id)
            noteFieldIndexService.createFromNote(note)
        }.onFailure { ex ->
            logger.error(ex) { "Failed to recreate invalid indexes for noteIds: $noteId" }
        }
    }

    private fun processNotCreatedIndexes(noteId: String) {
        runCatching {
            val note = noteService.fetchByExternalId(UUID.fromString(noteId))
            noteFieldIndexService.createFromNote(note)
        }.onFailure { ex ->
            logger.error(ex) { "Failed to create not-created indexes for noteIds: $noteId" }
        }
    }


}