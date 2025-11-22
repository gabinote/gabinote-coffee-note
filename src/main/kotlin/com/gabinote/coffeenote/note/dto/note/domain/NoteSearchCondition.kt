package com.gabinote.coffeenote.note.dto.note.domain

import org.springframework.data.domain.Pageable

data class NoteSearchCondition(
    val query: String,
    val owner: String,
    val pageable: Pageable,
    val highlightTag: String,
)
