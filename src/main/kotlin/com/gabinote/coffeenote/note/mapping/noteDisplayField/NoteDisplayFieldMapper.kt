package com.gabinote.coffeenote.note.mapping.noteDisplayField

import com.gabinote.coffeenote.note.domain.note.NoteDisplayField
import com.gabinote.coffeenote.note.dto.noteDisplayField.controller.NoteDisplayFieldResControllerDto
import com.gabinote.coffeenote.note.dto.noteDisplayField.service.NoteDisplayFieldResServiceDto
import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldCreateReqServiceDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(
    componentModel = "spring",
)
interface NoteDisplayFieldMapper {
    @Mapping(source = "overrideOrder", target = "order")
    fun toDisplayField(dto: NoteFieldCreateReqServiceDto, overrideOrder: Int): NoteDisplayField
    fun toResServiceDto(entity: NoteDisplayField): NoteDisplayFieldResServiceDto
    fun toResControllerDto(dto: NoteDisplayFieldResServiceDto): NoteDisplayFieldResControllerDto

}