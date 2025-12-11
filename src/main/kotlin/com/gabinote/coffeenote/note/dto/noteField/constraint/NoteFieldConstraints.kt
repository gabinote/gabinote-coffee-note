package com.gabinote.coffeenote.note.dto.noteField.constraint

object NoteFieldConstraints {
    const val FIELD_NAME_REGEX_STRING = "^[a-zA-Z0-9가-힣_\\-\\s]+$"
    val fieldNameRegex = Regex(FIELD_NAME_REGEX_STRING)
    const val FIELD_NAME_MAX_LENGTH = 50


    const val FIELD_VALUE_REGEX = "^[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9-_\\s]+$"


    // 입력 제한 자체는 가장 큰 LONG TEXT로 받음.
    const val FIELD_VALUE_MAX_LENGTH = 10000


}