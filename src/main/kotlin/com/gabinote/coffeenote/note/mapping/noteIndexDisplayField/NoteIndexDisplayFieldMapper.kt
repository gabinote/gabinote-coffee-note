package com.gabinote.coffeenote.note.mapping.noteIndexDisplayField

import com.gabinote.coffeenote.note.domain.noteIndex.IndexDisplayField
import com.gabinote.coffeenote.note.dto.noteIndexDisplayField.controller.IndexDisplayFieldResControllerDto
import com.gabinote.coffeenote.note.dto.noteIndexDisplayField.service.IndexDisplayFieldResServiceDto
import org.mapstruct.Mapper

@Mapper(
    componentModel = "spring",
)
interface NoteIndexDisplayFieldMapper {

    fun toDisplayFieldResServiceDto(field: IndexDisplayField): IndexDisplayFieldResServiceDto
    fun toDisplayFieldResControllerDto(field: IndexDisplayFieldResServiceDto): IndexDisplayFieldResControllerDto
}