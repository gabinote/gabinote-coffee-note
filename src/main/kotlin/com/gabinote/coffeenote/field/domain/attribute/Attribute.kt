package com.gabinote.coffeenote.field.domain.attribute

/**
 * 필드 타입의 속성을 나타내는 데이터 클래스
 * @property key 속성의 고유 키
 * @property value 속성의 값 집합
 * @author 황준서
 */
data class Attribute(
    val key: String,
    var value: Set<String>
) {
    /**
     * 속성 값 변경 메서드
     * @param newValue 새로운 값 집합
     */
    fun changeValue(newValue: Set<String>) {
        value = newValue
    }
}