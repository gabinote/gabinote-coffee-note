package com.gabinote.coffeenote.note.domain.noteHash

import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface NoteHashRepository : MongoRepository<NoteHash, ObjectId> {
    fun findByNoteId(noteId: ObjectId): NoteHash?
    fun findAllByOwner(owner: String, pageable: Pageable): Page<NoteHash>
}