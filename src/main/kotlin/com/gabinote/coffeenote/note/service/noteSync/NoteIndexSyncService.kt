package com.gabinote.coffeenote.note.service.noteSync

import com.gabinote.coffeenote.note.domain.note.NoteStatus
import com.gabinote.coffeenote.note.dto.note.vo.NoteExtIdHash
import com.gabinote.coffeenote.note.dto.noteIndex.vo.NoteIndexIdHash
import com.gabinote.coffeenote.note.service.note.NoteService
import com.gabinote.coffeenote.note.service.noteIndex.NoteIndexService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class NoteIndexSyncService(
    noteService: NoteService,
    private val noteIndexService: NoteIndexService,
) : NoteSyncService<NoteExtIdHash, NoteIndexStatus>(noteService) {


    override fun fetchCurrNote(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        pageable: Pageable,
    ): List<NoteExtIdHash> = noteService.getAllNoteExtIdHashWithBetweenModifiedDate(startDate, endDate, pageable)

    override fun fetchAllNote(
        beforeDate: LocalDateTime,
        pageable: Pageable,
    ): List<NoteExtIdHash> = noteService.getAllNoteExtIdHashBeforeModifiedDate(beforeDate, pageable)


    override fun compareWithNoteIndex(
        notes: List<NoteExtIdHash>,
    ): Map<NoteIndexStatus, List<String>> {
        val noteHashes = convertNoteExtIdHashToMap(notes)
        val indexHashes = noteIndexService.getAllByIds(noteHashes.keys.toList())

        val noteIdAndHashMap = convertNoteIndexIdHashToMap(indexHashes)

        val res = createIncorrectIndexMap()

        noteHashes.forEach { noteIdHash ->
            val indexHashes = noteIdAndHashMap[noteIdHash.key]
            val indexHash = indexHashes?.firstOrNull()

            when {

                isMultipleIndexExists(indexHashes) -> {
                    logger.debug { "noteId ${noteIdHash.key} has multiple index entries." }
                    res[NoteIndexStatus.MULTI_EXISTS]!!.add(noteIdHash.key)
                }

                isIndexNoteCreated(
                    noteHash = noteIdHash.value,
                    indexHash = indexHash,
                ) -> {
                    logger.debug { "noteId ${noteIdHash.key} is not indexed yet." }
                    res[NoteIndexStatus.NOT_FOUND]!!.add(noteIdHash.key)
                }

                isIndexNoteDeleted(
                    noteHash = noteIdHash.value,
                    indexHash = indexHash,
                ) -> {
                    logger.debug { "noteId ${noteIdHash.key} is deleted but index exists." }
                    res[NoteIndexStatus.NOT_DELETED]!!.add(noteIdHash.key)
                }

                isIndexHashDifferent(
                    noteHash = noteIdHash.value,
                    indexHash = indexHash,
                ) -> {
                    logger.debug { "noteId ${noteIdHash.key} has different hash. noteHash: ${noteIdHash.value.hash}, indexHash: $indexHash" }
                    res[NoteIndexStatus.INVALID]!!.add(noteIdHash.key)
                }
            }
        }

        return res
    }

    override fun recoverIncorrectIndexes(
        incorrectIndexMap: Map<NoteIndexStatus, List<String>>,
    ) {
        incorrectIndexMap[NoteIndexStatus.INVALID]?.let { noteIds ->
            processInvalidIndexNotes(
                noteIds = noteIds,
            )
        }

        incorrectIndexMap[NoteIndexStatus.NOT_FOUND]?.let { noteIds ->
            processNotFoundIndexNotes(
                noteIds = noteIds,
            )
        }

        incorrectIndexMap[NoteIndexStatus.NOT_DELETED]?.let { noteIds ->
            processNotDeletedIndexNotes(
                noteIds = noteIds,
            )
        }

        incorrectIndexMap[NoteIndexStatus.MULTI_EXISTS]?.let { noteIds ->
            processMultiExistsIndexNotes(
                noteIds = noteIds,
            )
        }
    }

    private fun processInvalidIndexNotes(
        noteIds: List<String>,
    ) {
        noteIds.forEach { noteId ->
            processRecreateIndex(
                noteId = noteId,
            )
        }
    }

    private fun processRecreateIndex(
        noteId: String,
    ) {
        runCatching {
            val id = UUID.fromString(noteId)
            val note = noteService.fetchByExternalId(id)
            noteIndexService.deleteByNoteId(id)
            noteIndexService.createFromNote(note)
        }.onFailure { ex ->
            logger.error(ex) { "failed to reindex noteId: $noteId" }
        }
    }

    private fun processNotFoundIndexNotes(
        noteIds: List<String>,
    ) {
        noteIds.forEach { noteId ->
            processCreateIndex(
                noteId = noteId,
            )
        }
    }

    private fun processCreateIndex(
        noteId: String,
    ) {
        runCatching {
            val id = UUID.fromString(noteId)
            val note = noteService.fetchByExternalId(id)
            noteIndexService.createFromNote(note)
        }.onFailure { ex ->
            logger.error(ex) { "failed to index noteId: $noteId" }
        }
    }

    private fun processNotDeletedIndexNotes(
        noteIds: List<String>,
    ) {
        noteIds.forEach { noteId ->
            processDeleteIndex(
                noteId = noteId,
            )
        }
    }

    private fun processDeleteIndex(
        noteId: String,
    ) {
        runCatching {
            val id = UUID.fromString(noteId)
            noteIndexService.deleteByNoteId(id)
        }.onFailure { ex ->
            logger.error(ex) { "failed to delete index for noteId: $noteId" }
        }
    }

    private fun processMultiExistsIndexNotes(
        noteIds: List<String>,
    ) {
        noteIds.forEach { noteId ->
            processRecreateIndex(
                noteId = noteId,
            )
        }
    }


    private fun isMultipleIndexExists(
        indexHashes: List<*>?,
    ): Boolean {
        // indexHash 가 2개 이상인 경우
        return indexHashes != null && indexHashes.size > 1
    }


    // 인덱스가 정상적으로 생성되지 않은 경우
    private fun isIndexNoteCreated(
        noteHash: NoteExtIdHash,
        indexHash: String?,
    ): Boolean {
        // note 가 삭제된 상태가 아니고, indexHash 가 없는 경우
        return noteHash.status != NoteStatus.DELETED && indexHash == null
    }


    // 인덱스가 삭제 정상적으로 삭제되지 않은 경우
    private fun isIndexNoteDeleted(
        noteHash: NoteExtIdHash,
        indexHash: String?,
    ): Boolean {
        // note 가 삭제된 상태이고, indexHash 가 존재하는 경우
        return noteHash.status == NoteStatus.DELETED && indexHash != null
    }


    // 정상적인 데이터가 아닌 경우
    private fun isIndexHashDifferent(
        noteHash: NoteExtIdHash,
        indexHash: String?,
    ): Boolean {
        // note 가 삭제된 상태가 아니고, 원본과 indexHash 가 다른 경우
        return noteHash.status != NoteStatus.DELETED && indexHash != null && noteHash.hash != indexHash
    }

    private fun convertNoteExtIdHashToMap(
        notes: List<NoteExtIdHash>,
    ): Map<String, NoteExtIdHash> {
        return notes.associateBy { it.externalId }
    }

    private fun convertNoteIndexIdHashToMap(
        notes: List<NoteIndexIdHash>,
    ): Map<String, List<String>> {
        return notes.groupBy { it.id }.mapValues { entry ->
            entry.value.map { it.noteHash }
        }
    }

    private fun createIncorrectIndexMap(): MutableMap<NoteIndexStatus, MutableList<String>> {
        val res: MutableMap<NoteIndexStatus, MutableList<String>> = mutableMapOf()
        NoteIndexStatus.entries.forEach {
            res[it] = mutableListOf()
        }
        return res
    }


}