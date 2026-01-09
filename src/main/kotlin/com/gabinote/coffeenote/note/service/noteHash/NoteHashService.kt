package com.gabinote.coffeenote.note.service.noteHash

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.common.util.hash.HashHelper
import com.gabinote.coffeenote.note.domain.note.Note
import org.springframework.stereotype.Service

@Service
class NoteHashService(
    private val hashHelper: HashHelper,
    private val objectMapper: ObjectMapper,
) {
    val exclude = setOf("id", "externalId", "hash", "createdDate", "modifiedDate", "isOpen", "status")
    fun create(note: Note): String {
        val noteMap: MutableMap<String, Any?> = objectMapper.convertValue(
            note,
            object : TypeReference<MutableMap<String, Any?>>() {}
        )
        exclude.forEach { noteMap.remove(it) }
        return hashHelper.generateHash(noteMap)
    }
}