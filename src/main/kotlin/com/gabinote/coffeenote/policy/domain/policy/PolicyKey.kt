package com.gabinote.coffeenote.policy.domain.policy

enum class PolicyKey(val key: String, val description: String) {
    NOTE_MAX_DISPLAYED_FIELD_COUNT("NOTE_MAX_DISPLAYED_FIELD_COUNT", "노트에 표시되는 최대 필드 개수"),
    NOTE_MAX_COUNT_PER_DEFAULT_USER("NOTE_MAX_COUNT_PER_DEFAULT_USER", "사용자 당 생성 가능한 노트의 최대 개수"),
}