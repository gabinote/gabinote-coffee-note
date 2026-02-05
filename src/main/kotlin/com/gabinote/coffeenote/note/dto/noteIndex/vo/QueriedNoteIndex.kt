package com.gabinote.coffeenote.note.dto.noteIndex.vo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.gabinote.coffeenote.common.util.meiliSearch.client.data.MatchesPosition
import com.gabinote.coffeenote.note.domain.noteIndex.NoteIndex

@JsonIgnoreProperties(ignoreUnknown = true)
data class QueriedNoteIndex(
    @field:JsonProperty("_matchesPosition")
    val matchesPosition: Map<String, List<MatchesPosition>> = emptyMap(),

    @field:JsonProperty("_formatted")
    val formatted: NoteIndex? = null,
)