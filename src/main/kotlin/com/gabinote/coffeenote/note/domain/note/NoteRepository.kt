package com.gabinote.coffeenote.note.domain.note


import com.gabinote.coffeenote.note.dto.note.vo.NoteOwnedItem
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface NoteRepository : MongoRepository<Note, ObjectId> {
    fun findByExternalId(externalId: String): Note?
    fun findAllByOwner(owner: String, pageable: Pageable): Slice<NoteOwnedItem>
    fun findAllByOwnerAndStatus(
        owner: String,
        status: NoteStatus = NoteStatus.ACTIVE,
        pageable: Pageable,
    ): Slice<NoteOwnedItem>

    fun <T> findAllByModifiedDateBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        pageable: Pageable,
        type: Class<T>,
    ): List<T>

    fun countAllByModifiedDateBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): Long

    fun <T> findAllByModifiedDateBefore(
        beforeDate: LocalDateTime,
        pageable: Pageable,
        type: Class<T>,
    ): List<T>

    fun countAllByModifiedDateBefore(
        beforeDate: LocalDateTime,
    ): Long

    fun countByOwner(owner: String): Long
    fun deleteAllByOwner(owner: String)
}