package com.gabinote.coffeenote.note.service.noteDisplayField

import com.gabinote.coffeenote.note.domain.note.NoteDisplayField
import com.gabinote.coffeenote.note.dto.noteField.service.NoteFieldCreateReqServiceDto
import com.gabinote.coffeenote.note.mapping.noteDisplayField.NoteDisplayFieldMapper
import org.springframework.stereotype.Service

@Service
class NoteDisplayFieldService(
    private val noteDisplayFieldMapper: NoteDisplayFieldMapper,
) {

    fun create(dto: List<NoteFieldCreateReqServiceDto>): List<NoteDisplayField> {
        val displayFields = dto.filter { it.isDisplay }.sortedBy { it.order }
        var order = 0
        val data: MutableList<NoteDisplayField> = mutableListOf()
        for (displayField in displayFields) {
            if (!displayField.isDisplay) {
                continue
            }
            val displayData = noteDisplayFieldMapper.toDisplayField(
                dto = displayField,
                overrideOrder = order
            )
            data.add(displayData)
            order++
        }
        return data
    }
}