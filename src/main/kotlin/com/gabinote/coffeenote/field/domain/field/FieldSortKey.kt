package com.gabinote.coffeenote.field.domain.field

import com.gabinote.coffeenote.common.domain.base.BaseSortKey

/**
 * 필드 도메인 엔티티 정렬에 사용되는 키 정의 열거형
 * @author 황준서
 */
enum class FieldSortKey(
    override val key: String,
) : BaseSortKey {
    /**
     * 필드 이름으로 정렬
     */
    NAME("name"),

    /**
     * 기본 필드 여부로 정렬
     */
    DEFAULT("default"),

    /**
     * 필드 타입으로 정렬
     */
    TYPE("type"),

    /**
     * 소유자로 정렬
     */
    OWNER("owner"),

    /**
     * ID로 정렬
     */
    ID("id"),

    /**
     * 외부 식별자로 정렬
     */
    EXTERNAL_ID("externalId");
}