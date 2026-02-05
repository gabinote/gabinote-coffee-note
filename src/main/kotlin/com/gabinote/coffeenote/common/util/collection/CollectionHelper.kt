package com.gabinote.coffeenote.common.util.collection

/**
 * 컬렉션 관련 유틸리티 함수 모음
 */
object CollectionHelper {
    /**
     * 컬렉션의 첫 번째 요소를 문자열로 반환
     * 컬렉션이 비어있으면 빈 문자열을 반환
     */
    fun Collection<*>.firstOrEmptyString(): String {
        return this.firstOrNull()?.toString() ?: ""
    }
}