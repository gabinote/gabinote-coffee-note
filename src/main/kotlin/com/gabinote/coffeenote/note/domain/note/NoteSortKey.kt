package com.gabinote.coffeenote.note.domain.note

import com.gabinote.coffeenote.common.domain.base.BaseSortKey

enum class NoteSortKey(
    override val key: String,
) : BaseSortKey {
    /**
     * 노트 제목으로 정렬
     */
    TITLE("title"),

    /**
     * 노트 작성일로 정렬
     */
    CREATED_AT("createdAt"),

    /**
     * 노트 수정일로 정렬
     */
    UPDATED_AT("updatedAt")
}