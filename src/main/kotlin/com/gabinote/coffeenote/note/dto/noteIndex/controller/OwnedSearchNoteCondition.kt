package com.gabinote.coffeenote.note.dto.noteIndex.controller

import com.gabinote.coffeenote.note.dto.noteFieldIndex.constraint.NoteFieldIndexConstraints
import com.gabinote.coffeenote.note.dto.noteIndex.constraint.NoteIndexConstraints
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class OwnedSearchNoteCondition(
    @field:Pattern(
        regexp = NoteIndexConstraints.SEARCH_STRING_REGEX,
        message = "special characters are not allowed"
    )
    @field:Length(
        max = NoteIndexConstraints.SEARCH_MAX_LENGTH,
        message = "query must be at most ${NoteIndexConstraints.SEARCH_MAX_LENGTH} characters long"
    )
    @field:NotBlank(message = "query must not be blank")
    val query: String,

    @field:Pattern(
        regexp = NoteFieldIndexConstraints.HIGHLIGHT_TAG_STRING_REGEX,
        message = "Only alphabetic characters are allowed for highlightTag"
    )
    @field:Length(
        max = NoteFieldIndexConstraints.HIGHLIGHT_TAG_MAX_LENGTH,
        message = "highlightTag must be at most ${NoteFieldIndexConstraints.HIGHLIGHT_TAG_MAX_LENGTH} characters long"
    )
    @field:NotBlank(message = "highlightTag must not be blank")
    val highlightTag: String,
)