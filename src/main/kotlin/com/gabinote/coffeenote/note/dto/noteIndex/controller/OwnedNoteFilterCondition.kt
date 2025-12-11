package com.gabinote.coffeenote.note.dto.noteIndex.controller

import com.gabinote.coffeenote.note.dto.noteFieldIndex.constraint.NoteFieldIndexConstraints
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime

data class OwnedNoteFilterCondition(


    val fieldOptions: Map<String, List<String>> = emptyMap(),


    val createdDateStart: LocalDateTime? = null,
    val createdDateEnd: LocalDateTime? = null,

    val modifiedDateStart: LocalDateTime? = null,
    val modifiedDateEnd: LocalDateTime? = null,


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
) {
    @AssertTrue(message = "createdDateStart or createdDateEnd is invalid")
    fun isCreatedDateValid(): Boolean {
        return validationDate(createdDateStart, createdDateEnd)
    }

    @AssertTrue(message = "modifiedDateStart or modifiedDateEnd is invalid")
    fun isModifiedDateValid(): Boolean {
        return validationDate(modifiedDateStart, modifiedDateEnd)
    }

    private fun validationDate(start: LocalDateTime?, end: LocalDateTime?): Boolean {
        // case 1 : 둘다 null
        if (start == null && end == null) {
            return true
        }

        // case 2 : 둘증 하나만 null
        if (start == null || end == null) {
            return true
        }

        // case 3 : 둘다 존재하면, start 가 end 이전인지 확인
        if (start.isAfter(end)) {
            return false
        }

        return true
    }
}