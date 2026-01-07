package com.gabinote.coffeenote.note.consumer.noteIndexSink.data

import com.gabinote.coffeenote.note.domain.note.Note

data class BeforeAfterNote(
    val before: Note? = null,
    val after: Note? = null,
)