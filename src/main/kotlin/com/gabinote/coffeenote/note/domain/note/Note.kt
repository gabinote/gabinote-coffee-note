package com.gabinote.coffeenote.note.domain.note

import com.gabinote.coffeenote.common.util.auditor.extId.ExternalId
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "notes")
data class Note(
    @Id
    var id: ObjectId? = null,

    @ExternalId
    var externalId: String? = null,

    var title: String,

    var thumbnail: String? = null,

    @CreatedDate
    var createdDate: LocalDateTime? = null,

    @LastModifiedDate
    var modifiedDate: LocalDateTime? = null,

    var fields: List<NoteField> = emptyList(),

    var displayFields: List<NoteDisplayField> = emptyList(),

    @JvmField
    var isOpen: Boolean = false,

    var owner: String,

    var hash: String? = null,

    ) {
    fun changeField(fields: List<NoteField>) {
        this.fields = fields
    }

    fun isOwner(userId: String): Boolean {
        return this.owner == userId
    }

    fun changeFields(newFields: List<NoteField>) {
        this.fields = newFields
    }

    fun changeDisplayFields(newDisplayFields: List<NoteDisplayField>) {
        this.displayFields = newDisplayFields
    }

    fun changeHash(newHash: String) {
        this.hash = newHash
    }

    fun setFields(fields: List<NoteField>, displayFields: List<NoteDisplayField>) {
        this.fields = fields
        this.displayFields = displayFields
    }

    fun updateFields(newNote: Note) {
        this.fields = newNote.fields
        this.displayFields = newNote.displayFields
    }


}