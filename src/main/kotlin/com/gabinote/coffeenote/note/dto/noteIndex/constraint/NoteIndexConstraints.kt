package com.gabinote.coffeenote.note.dto.noteIndex.constraint

object NoteIndexConstraints {
    const val SEARCH_MAX_LENGTH = 50

    // 공백 포함 한글, 영어, 숫자, 와일드카드 허용
    const val SEARCH_STRING_REGEX = "^(?:[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9\\s]+|\\*)$"


}