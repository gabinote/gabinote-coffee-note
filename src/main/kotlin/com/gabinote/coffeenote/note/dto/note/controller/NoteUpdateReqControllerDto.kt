package com.gabinote.coffeenote.note.dto.note.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.common.util.img.ImgValidationHelper
import com.gabinote.coffeenote.note.dto.note.constraint.NoteConstraints
import com.gabinote.coffeenote.note.dto.noteField.controller.NoteFieldCreateReqControllerDto
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NoteUpdateReqControllerDto(

    @field:NotBlank(message = "name must not be blank")
    @field:Length(
        max = NoteConstraints.TITLE_MAX_LENGTH,
        message = "title must be at most ${NoteConstraints.TITLE_MAX_LENGTH} characters"
    )
    var title: String,

    @field:Pattern(message = "thumbnail must be uuid + format ", regexp = ImgValidationHelper.UUID_REGEX)
    @field:Length(
        max = NoteConstraints.THUMBNAIL_MAX_LENGTH,
        message = "thumbnail must be at most ${NoteConstraints.THUMBNAIL_MAX_LENGTH} characters"
    )
    var thumbnail: String? = null,

    @field:Valid
    @field:Size(
        min = NoteConstraints.FIELD_MIN_COUNT,
        max = NoteConstraints.FIELD_MAX_COUNT,
        message = "fields must be between ${NoteConstraints.FIELD_MIN_COUNT} and ${NoteConstraints.FIELD_MAX_COUNT}"
    )
    var fields: List<NoteFieldCreateReqControllerDto> = emptyList(),

    @JvmField
    var isOpen: Boolean,

    )