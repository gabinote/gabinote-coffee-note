package com.gabinote.coffeenote.note.domain.note


import com.gabinote.coffeenote.note.domain.note.vo.NoteOwnedItem
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface NoteRepository : MongoRepository<Note, ObjectId> {
    fun findByExternalId(externalId: String): Note?
    fun findAllByOwner(owner: String, pageable: Pageable): Slice<NoteOwnedItem>
    fun countByOwner(owner: String): Long
    fun deleteAllByOwner(owner: String)
}