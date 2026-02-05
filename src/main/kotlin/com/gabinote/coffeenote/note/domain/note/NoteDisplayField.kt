package com.gabinote.coffeenote.note.domain.note

data class NoteDisplayField(
    val name: String,
    val icon: String,
    val values: Set<String>,
    // NoteField 중 표시될 필드들에 대한 표시 순서임.
    // 즉 NoteField 의 order 와 값이 다를 수 있으나, 순서는 동일함.
    val order: Int = 0,
)