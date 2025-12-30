package com.gabinote.coffeenote.note.consumer.noteIndexSink

import com.gabinote.coffeenote.note.service.noteFieldIndex.NoteFieldIndexService
import com.gabinote.coffeenote.note.service.noteIndex.NoteIndexService
import org.springframework.stereotype.Service

@Service
class NoteIndexSinkConsumer(
    private val noteIndexService: NoteIndexService,
    private val noteFieldIndexService: NoteFieldIndexService,
)