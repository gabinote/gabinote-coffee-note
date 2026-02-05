package com.gabinote.coffeenote.note.dto.noteIndex.domain

import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

data class NoteFilterCondition(
    val fieldOptions: Map<String, List<String>> = emptyMap(),

    val owner: String,

    val createdDateStart: LocalDateTime? = null,
    val createdDateEnd: LocalDateTime? = null,

    val modifiedDateStart: LocalDateTime? = null,
    val modifiedDateEnd: LocalDateTime? = null,

    val pageable: Pageable,
    val highlightTag: String,
)
