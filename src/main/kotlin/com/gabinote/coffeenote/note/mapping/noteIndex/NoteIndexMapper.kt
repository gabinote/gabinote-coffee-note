package com.gabinote.coffeenote.note.mapping.noteIndex

import com.gabinote.coffeenote.common.mapping.time.TimeMapper
import com.gabinote.coffeenote.note.domain.noteIndex.NoteIndex
import com.gabinote.coffeenote.note.dto.noteIndex.controller.NoteIndexResControllerDto
import com.gabinote.coffeenote.note.dto.noteIndex.service.NoteIndexResServiceDto
import com.gabinote.coffeenote.note.mapping.noteIndexDisplayField.NoteIndexDisplayFieldMapper
import org.mapstruct.Mapper

@Mapper(
    componentModel = "spring",
    uses = [NoteIndexDisplayFieldMapper::class, TimeMapper::class]
)
interface NoteIndexMapper {
    fun toResServiceDto(noteIndex: NoteIndex): NoteIndexResServiceDto
    fun toResControllerDto(dto: NoteIndexResServiceDto): NoteIndexResControllerDto
}