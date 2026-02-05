package com.gabinote.coffeenote.note.dto.noteFieldIndex.controller

import com.gabinote.coffeenote.note.dto.noteFieldIndex.constraint.NoteFieldIndexConstraints
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class NoteFieldNameFacetsSearchCondition(
    //한글 영어 숫자 언더바 및 와일드카드(*) 허용
    @field:Pattern(
        regexp = NoteFieldIndexConstraints.SEARCH_NAME_STRING_REGEX,
        message = "special characters are not allowed"
    )
    @field:Length(
        max = NoteFieldIndexConstraints.SEARCH_NAME_MAX_LENGTH,
        message = "query must be at most ${NoteFieldIndexConstraints.SEARCH_NAME_MAX_LENGTH} characters long"
    )
    @field:NotBlank(message = "query must not be blank")
    val query: String = "*",

    )