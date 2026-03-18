package com.gabinote.coffeenote.policy.domain.policy

enum class PolicyKey(val key: String, val description: String) {
    NOTE_MAX_DISPLAYED_FIELD_COUNT("note.field.max-display", "노트에 표시되는 최대 필드 개수"),
    NOTE_MAX_COUNT_PER_DEFAULT_USER("note.user.per-cnt", "사용자 당 생성 가능한 노트의 최대 개수"),
    USER_REGISTER_BASE_ROLE(
        key = "user.register.base_role",
        description = "사용자 가입 시 기본으로 할당되는 Role",
    ),
    USER_ENABLED_REGISTER(
        key = "user.register.enabled",
        description = "사용자 가입 가능 여부",
    ),
    USER_PURGE_CUTOFF_DAYS(
        key = "user.withdraw.purge.cutoff_days",
        description = "사용자 완전 삭제 처리까지의 유예 기간 (일)",
    ),
}