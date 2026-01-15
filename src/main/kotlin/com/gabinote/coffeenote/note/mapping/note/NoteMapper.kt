package com.gabinote.coffeenote.note.mapping.note

import com.gabinote.coffeenote.note.domain.note.Note
import com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto
import com.gabinote.coffeenote.note.dto.note.controller.NoteListResControllerDto
import com.gabinote.coffeenote.note.dto.note.controller.NoteResControllerDto
import com.gabinote.coffeenote.note.dto.note.controller.NoteUpdateReqControllerDto
import com.gabinote.coffeenote.note.dto.note.service.NoteCreateReqServiceDto
import com.gabinote.coffeenote.note.dto.note.service.NoteListResServiceDto
import com.gabinote.coffeenote.note.dto.note.service.NoteResServiceDto
import com.gabinote.coffeenote.note.dto.note.service.NoteUpdateReqServiceDto
import com.gabinote.coffeenote.note.dto.note.vo.NoteOwnedItem
import com.gabinote.coffeenote.note.mapping.noteDisplayField.NoteDisplayFieldMapper
import com.gabinote.coffeenote.note.mapping.noteField.NoteFieldMapper
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import java.util.*

@Mapper(
    componentModel = "spring",
    uses = [NoteDisplayFieldMapper::class, NoteFieldMapper::class],
)
interface NoteMapper {
    fun toNoteResServiceDto(note: Note): NoteResServiceDto
    fun toListResServiceDto(noteOwnedItem: NoteOwnedItem): NoteListResServiceDto

    fun toResControllerDto(dto: NoteResServiceDto): NoteResControllerDto
    fun toListResControllerDto(dto: NoteListResServiceDto): NoteListResControllerDto

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "fields", expression = "java(java.util.Collections.emptyList())")
    @Mapping(target = "displayFields", expression = "java(java.util.Collections.emptyList())")
    @Mapping(target = "status", constant = "ACTIVE")
    fun toNote(dto: NoteCreateReqServiceDto): Note

    fun toCreateReqServiceDto(dto: NoteUpdateReqServiceDto): NoteCreateReqServiceDto
    fun toCreateReqServiceDto(dto: NoteCreateReqControllerDto, owner: String): NoteCreateReqServiceDto
    fun toUpdateReqServiceDto(dto: NoteUpdateReqControllerDto, externalId: UUID, owner: String): NoteUpdateReqServiceDto


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "displayFields", ignore = true)
    @Mapping(target = "fields", ignore = true)
    fun updateNoteFromEntity(
        source: Note,
        @MappingTarget target: Note,
    )
}