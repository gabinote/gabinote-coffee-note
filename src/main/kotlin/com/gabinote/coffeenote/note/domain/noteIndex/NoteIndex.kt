package com.gabinote.coffeenote.note.domain.noteIndex

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class NoteIndex(
    // Note의 external ID
    val id: String,
    var title: String,
    val owner: String,
    val createdDate: Long,
    val modifiedDate: Long,
    val displayFields: List<IndexDisplayField> = emptyList(),
    var filters: Map<String, List<String>>,
    var synchronizedAt: Long,
    var noteHash: String,
) {
    fun changeFilters(newFilters: Map<String, List<String>>) {
        this.filters = newFilters
    }

    fun changeTitle(newTitle: String) {
        this.title = newTitle
    }

}