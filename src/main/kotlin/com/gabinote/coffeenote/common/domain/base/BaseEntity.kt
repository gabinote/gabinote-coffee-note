package com.gabinote.coffeenote.common.domain.base

/**
 * 모든 도메인 엔티티의 기본 클래스
 * ID 필드와 기본적인 equals/hashCode 구현을 제공
 * @param T ID 필드의 타입
 * @author 황준서
 */
abstract class BaseEntity<T>(
    /**
     * 엔티티의 고유 식별자
     */
    open var id: T? = null,

    ) {
    /**
     * 두 엔티티의 동등성 비교 - ID 기반으로 비교
     * @param other 비교할 객체
     * @return 동등성 여부
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseEntity<*>

        return id == other.id
    }

    /**
     * 엔티티의 해시코드 반환 - ID 기반으로 계산
     * @return 해시코드 값
     */
    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}