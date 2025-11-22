package com.gabinote.coffeenote.note.mapping.noteHash

import com.gabinote.coffeenote.note.domain.note.Note
import com.gabinote.coffeenote.note.domain.noteHash.NoteHash
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(
    componentModel = "spring",
)
interface NoteHashMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "note.id", target = "noteId")
    fun toHash(note: Note, hash: String): NoteHash

}