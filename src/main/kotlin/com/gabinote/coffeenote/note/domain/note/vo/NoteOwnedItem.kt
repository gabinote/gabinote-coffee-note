package com.gabinote.coffeenote.note.domain.note.vo

import com.gabinote.coffeenote.note.domain.note.NoteDisplayField
import com.gabinote.coffeenote.note.domain.note.NoteStatus
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class NoteOwnedItem(
    var id: ObjectId,
    var externalId: String,

    var title: String,

    var thumbnail: String? = null,

    var createdDate: LocalDateTime? = null,
    var modifiedDate: LocalDateTime? = null,

    var displayFields: List<NoteDisplayField> = emptyList(),

    @JvmField
    var isOpen: Boolean = false,

    var owner: String,

    var status: NoteStatus,
)