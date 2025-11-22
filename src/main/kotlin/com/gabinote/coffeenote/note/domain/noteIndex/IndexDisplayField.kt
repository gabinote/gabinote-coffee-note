package com.gabinote.coffeenote.note.domain.noteIndex

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class IndexDisplayField(
    val name: String,
    val tag: String,
    val value: List<String>,
    val order: Int = 0,
)