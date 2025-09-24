package com.gabinote.coffeenote.template.enums

enum class TemplateUserSearchScope {
    /**
     * 기본 템플릿만 검색
     */
    DEFAULT,

    /**
     * 사용자 소유 템플릿만 검색
     */
    OWNED,

    /**
     * 모든 템플릿 검색 (기본 필드와 사용자 소유 필드)
     */
    ALL
}