package com.gabinote.coffeenote.policy.domain.policy

enum class PolicyKey(val key: String, val description: String) {
    NOTE_MAX_DISPLAYED_FIELD_COUNT("note.field.max-display", "노트에 표시되는 최대 필드 개수"),
    NOTE_MAX_COUNT_PER_DEFAULT_USER("note.user.per-cnt", "사용자 당 생성 가능한 노트의 최대 개수"),
}