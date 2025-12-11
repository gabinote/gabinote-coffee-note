package com.gabinote.coffeenote.note.service.note

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotFound
import com.gabinote.coffeenote.common.util.exception.service.ResourceQuotaLimit
import com.gabinote.coffeenote.note.domain.note.Note
import com.gabinote.coffeenote.note.domain.note.NoteRepository
import com.gabinote.coffeenote.note.dto.note.service.NoteCreateReqServiceDto
import com.gabinote.coffeenote.note.dto.note.service.NoteListResServiceDto
import com.gabinote.coffeenote.note.dto.note.service.NoteResServiceDto
import com.gabinote.coffeenote.note.dto.note.service.NoteUpdateReqServiceDto
import com.gabinote.coffeenote.note.mapping.note.NoteMapper
import com.gabinote.coffeenote.note.service.note.strategy.GetNoteByExternalIdStrategyFactory
import com.gabinote.coffeenote.note.service.note.strategy.GetNoteByExternalIdStrategyType
import com.gabinote.coffeenote.note.service.noteDisplayField.NoteDisplayFieldService
import com.gabinote.coffeenote.note.service.noteField.NoteFieldService
import com.gabinote.coffeenote.note.service.noteHash.NoteHashService
import com.gabinote.coffeenote.policy.domain.policy.PolicyKey
import com.gabinote.coffeenote.policy.service.policy.PolicyService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

val logger = KotlinLogging.logger {}

@Service
class NoteService(
    private val noteRepository: NoteRepository,
    private val noteMapper: NoteMapper,
    private val getNoteByExternalIdStrategyFactory: GetNoteByExternalIdStrategyFactory,
    private val noteFieldService: NoteFieldService,
    private val policyService: PolicyService,
    private val noteHashService: NoteHashService,
    private val noteDisplayFieldService: NoteDisplayFieldService,

    ) {
    fun fetchById(id: ObjectId): Note {
        return noteRepository.findByIdOrNull(id) ?: throw ResourceNotFound(
            name = "Note",
            identifier = id.toString(),
            identifierType = "id"
        )
    }

    fun fetchByExternalId(externalId: UUID): Note {
        val externalIdStr = externalId.toString()
        return noteRepository.findByExternalId(externalIdStr) ?: throw ResourceNotFound(
            name = "Note",
            identifier = externalIdStr,
            identifierType = "externalId"
        )
    }

    fun getByExternalId(
        externalId: UUID,
        requestor: String,
        strategyType: GetNoteByExternalIdStrategyType,
    ): NoteResServiceDto {
        val note = fetchByExternalId(externalId)
        val strategy = getNoteByExternalIdStrategyFactory.getStrategy(strategyType)
        strategy.validate(requestor = requestor, note = note)
        return noteMapper.toNoteResServiceDto(note)
    }

    fun getOpenByExternalId(
        externalId: UUID,
        requestor: String = "",
    ): NoteResServiceDto {
        return getByExternalId(
            externalId = externalId,
            requestor = requestor,
            strategyType = GetNoteByExternalIdStrategyType.OPENED
        )
    }

    fun getOwnedByExternalId(
        externalId: UUID,
        requestor: String,
    ): NoteResServiceDto {
        return getByExternalId(
            externalId = externalId,
            requestor = requestor,
            strategyType = GetNoteByExternalIdStrategyType.OWNED
        )
    }

    fun getAllByOwner(owner: String, pageable: Pageable): Slice<NoteListResServiceDto> {
        val notes = noteRepository.findAllByOwner(owner = owner, pageable = pageable)
        return notes.map { note ->
            noteMapper.toListResServiceDto(note)
        }
    }

    fun create(dto: NoteCreateReqServiceDto): NoteResServiceDto {
        checkMaxNoteCount(dto.owner)
        val note = createNote(dto)
        val saved = noteRepository.save(note)
        return noteMapper.toNoteResServiceDto(saved)
    }

    fun update(dto: NoteUpdateReqServiceDto): NoteResServiceDto {
        val existsNote = fetchByExternalId(dto.externalId)
        checkOwnership(existsNote, dto.owner)
        val createReq = noteMapper.toCreateReqServiceDto(dto)
        val newNote = createNote(createReq)
        if (!isChange(existsNote, newNote)) {
            return noteMapper.toNoteResServiceDto(existsNote)
        }
        noteMapper.updateNoteFromEntity(source = newNote, target = existsNote)
        existsNote.updateFields(newNote = newNote)
        val updatedNote = noteRepository.save(existsNote)
        return noteMapper.toNoteResServiceDto(updatedNote)
    }

    fun deleteByExternalId(externalId: UUID, owner: String) {
        val existsNote = fetchByExternalId(externalId)
        checkOwnership(existsNote, owner)
        noteRepository.delete(existsNote)
    }

    fun deleteAllByOwner(owner: String) {
        noteRepository.deleteAllByOwner(owner)
    }

    private fun isChange(origin: Note, newNote: Note): Boolean {
        return origin.hash != newNote.hash || origin.isOpen != newNote.isOpen
    }

    private fun createNote(
        dto: NoteCreateReqServiceDto,
    ): Note {
        val fields = noteFieldService.create(dto.fields)
        val displayFields = noteDisplayFieldService.create(dto.fields)
        val newNote = noteMapper.toNote(dto = dto).apply {
            setFields(fields, displayFields)
        }
        applyHash(newNote)
        return newNote
    }

    private fun applyHash(note: Note) {

        val hash = noteHashService.create(note)
        note.changeHash(hash)
    }


    private fun checkOwnership(note: Note, owner: String) {
        if (note.owner != owner) {
            throw ResourceNotFound(
                name = "Note",
                identifier = note.externalId.toString(),
                identifierType = "externalId"
            )
        }
    }


    private fun checkMaxNoteCount(owner: String) {
        val maxNoteCount = policyService.getByKey(PolicyKey.NOTE_MAX_COUNT_PER_DEFAULT_USER).toLong()
        val currentNoteCount = noteRepository.countByOwner(owner)
        if (currentNoteCount >= maxNoteCount) {
            throw ResourceQuotaLimit(
                name = "Note",
                quotaType = "Max Note Count Per User",
                quotaLimit = maxNoteCount
            )
        }
    }


}