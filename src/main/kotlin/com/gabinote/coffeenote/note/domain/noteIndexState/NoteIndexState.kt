package com.gabinote.coffeenote.note.domain.noteIndexState

import com.gabinote.coffeenote.note.domain.note.NoteStatus
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "note_index_state")
data class NoteIndexState(
    @Id
    var id: ObjectId? = null,

    var noteId: String,

    var noteIndexType: NoteIndexType,

    var lastNoteStatus: NoteStatus,

    // 해당 동기화 기준 시점의 해시값
    var currHash: String,

    // NoteIndex,NoteField 동기화가 완료된 시점
    // 해당 인덱스와 동일한 값을 가짐
    var syncAt: LocalDateTime,
)