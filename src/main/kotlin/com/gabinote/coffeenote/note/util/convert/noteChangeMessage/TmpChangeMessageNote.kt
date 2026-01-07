package com.gabinote.coffeenote.note.util.convert.noteChangeMessage

import com.fasterxml.jackson.annotation.JsonProperty
import com.gabinote.coffeenote.note.domain.note.NoteDisplayField
import com.gabinote.coffeenote.note.domain.note.NoteField
import org.bson.types.ObjectId

data class TmpChangeMessageNote(

    @JsonProperty("_id")
    var id: ObjectId,
    var externalId: String,
    var title: String,
    var thumbnail: String? = null,
    var fields: List<NoteField> = emptyList(),
    var displayFields: List<NoteDisplayField> = emptyList(),
    @JvmField
    var isOpen: Boolean = false,
    var owner: String,
    var hash: String,
)