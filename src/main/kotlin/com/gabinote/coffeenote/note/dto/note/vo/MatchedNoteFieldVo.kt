package com.gabinote.coffeenote.note.dto.note.vo

import org.bson.types.ObjectId
import java.time.LocalDateTime

data class MatchedNoteFieldVo(
    val id: ObjectId,
    val externalId: String,
    val createdDate: LocalDateTime,
    val modifiedDate: LocalDateTime,
    val matchedFields: List<MatchedNoteFieldVo> = emptyList(),
)