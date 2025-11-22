package com.gabinote.coffeenote.note.domain.noteHash

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "note_hashes")
data class NoteHash(
    @Id
    var id: ObjectId? = null,

    var noteId: ObjectId,

    @CreatedDate
    var createdDate: LocalDateTime? = null,

    var hash: String,

    var owner: String,

    ) {
    fun changeHash(hash: String) {
        this.hash = hash
    }
}