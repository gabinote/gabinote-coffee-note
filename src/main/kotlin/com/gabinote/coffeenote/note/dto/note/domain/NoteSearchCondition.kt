package com.gabinote.coffeenote.note.dto.note.domain

import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

data class NoteSearchCondition(
    val fieldOptions: Map<String, Set<String>> = emptyMap(),

    val owner: String,

    val createdDateStart: LocalDateTime? = null,
    val createdDateEnd: LocalDateTime? = null,

    val modifiedDateStart: LocalDateTime? = null,
    val modifiedDateEnd: LocalDateTime? = null,

    val pageable: Pageable
)
