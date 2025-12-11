package com.gabinote.coffeenote.note.dto.noteFieldIndex.controller

import com.gabinote.coffeenote.note.dto.noteFieldIndex.constraint.NoteFieldIndexConstraints
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class NoteFieldValuesFacetsSearchCondition(
    //한글 영어 숫자 공백 및 와일드카드(*) 허용
    @field:Pattern(
        regexp = NoteFieldIndexConstraints.SEARCH_VALUE_STRING_REGEX,
        message = "special characters are not allowed"
    )
    @field:Length(
        max = NoteFieldIndexConstraints.SEARCH_VALUE_MAX_LENGTH,
        message = "query must be at most ${NoteFieldIndexConstraints.SEARCH_VALUE_MAX_LENGTH} characters long"
    )
    @field:NotBlank(message = "query must not be blank")
    val query: String = "*",

    )