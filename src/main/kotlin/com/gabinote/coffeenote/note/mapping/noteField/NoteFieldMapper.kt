package com.gabinote.coffeenote.note.mapping.noteField

import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapper
import com.gabinote.coffeenote.field.mapping.fieldType.FieldTypeMapper
import com.gabinote.coffeenote.note.domain.note.NoteField
import com.gabinote.coffeenote.note.dto.noteField.controller.NoteFieldCreateReqControllerDto
import com.gabinote.coffeenote.note.dto.noteField.controller.NoteFieldResControllerDto
import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldCreateReqServiceDto
import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldResServiceDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(
    componentModel = "spring",
    uses = [FieldTypeMapper::class, AttributeMapper::class]
)
interface NoteFieldMapper {

    @Mapping(target = "attributes", expression = "java(java.util.Collections.emptySet())")
    fun toNoteField(dto: NoteFieldCreateReqServiceDto): NoteField
    fun toResServiceDto(entity: NoteField): NoteFieldResServiceDto
    fun toResControllerDto(dto: NoteFieldResServiceDto): NoteFieldResControllerDto

    fun toCreateReqServiceDto(dto: NoteFieldCreateReqControllerDto): NoteFieldCreateReqServiceDto
}