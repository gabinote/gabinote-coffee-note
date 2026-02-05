package com.gabinote.coffeenote.note.dto.noteFieldIndex.constraint

object NoteFieldIndexConstraints {
    const val SEARCH_VALUE_MAX_LENGTH = 100
    const val SEARCH_VALUE_STRING_REGEX = "^([가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9-_]+|\\*)$"

    const val SEARCH_NAME_MAX_LENGTH = 50
    const val SEARCH_NAME_STRING_REGEX = "^(?:[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9]+|\\*)$"

    const val HIGHLIGHT_TAG_MAX_LENGTH = 20
    const val HIGHLIGHT_TAG_STRING_REGEX = "^[a-zA-Z]+$"

}