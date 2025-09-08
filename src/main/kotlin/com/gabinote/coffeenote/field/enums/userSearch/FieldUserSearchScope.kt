package com.gabinote.coffeenote.field.enums.userSearch

/**
 * 사용자 필드 검색 범위를 정의하는 열거형
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
enum class FieldUserSearchScope {
    /**
     * 기본 필드만 검색
     */
    DEFAULT,

    /**
     * 사용자 소유 필드만 검색
     */
    OWNED,

    /**
     * 모든 필드 검색 (기본 필드와 사용자 소유 필드)
     */
    ALL
}