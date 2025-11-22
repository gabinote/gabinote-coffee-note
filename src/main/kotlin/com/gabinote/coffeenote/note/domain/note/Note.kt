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

    ) {
    fun changeField(fields: List<NoteField>) {
        this.fields = fields
    }

    fun isOwner(userId: String): Boolean {
        return this.owner == userId
    }
}