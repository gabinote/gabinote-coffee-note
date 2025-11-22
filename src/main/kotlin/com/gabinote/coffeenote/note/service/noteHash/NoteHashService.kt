package com.gabinote.coffeenote.note.service.noteHash

import com.gabinote.coffeenote.common.util.hash.HashHelper
import com.gabinote.coffeenote.note.domain.note.Note
import com.gabinote.coffeenote.note.domain.noteHash.NoteHash
import com.gabinote.coffeenote.note.domain.noteHash.NoteHashRepository
import com.gabinote.coffeenote.note.mapping.noteHash.NoteHashMapper
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class NoteHashService(
    private val hashHelper: HashHelper,
    private val noteHashRepository: NoteHashRepository,
    private val noteHashMapper: NoteHashMapper,
) {

    fun fetchByNoteId(noteId: ObjectId): NoteHash {
        return noteHashRepository.findByNoteId(noteId)
            ?: throw NoSuchElementException("NoteHash not found for noteId: $noteId")
    }

    fun create(note: Note) {
        val hash = hashHelper.generateHash(note)
        val noteHash = noteHashMapper.toHash(note, hash)
        noteHashRepository.save(noteHash)
    }

    fun update(note: Note) {
        val hash = hashHelper.generateHash(note)

        val existingNoteHash = noteHashRepository.findByNoteId(
            note.id ?: throw IllegalArgumentException("Note id cannot be null when updating NoteHash")
        ) ?: throw NoSuchElementException("NoteHash not found for noteId: ${note.id}")

        existingNoteHash.changeHash(hash)
        noteHashRepository.save(existingNoteHash)
    }
}