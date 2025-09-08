package com.gabinote.coffeenote.field.domain.fieldType

/**
 * 필드 타입 속성 키를 정의하는 데이터 클래스
 * 각 필드 타입이 지원하는 속성의 키와 유효성 검사 함수를 포함
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
data class FieldTypeAttributeKey(
    /**
     * 속성의 고유 키
     */
    val key: String,

    /**
     * 속성 값의 유효성을 검사하는 함수
     * @param value 검사할 속성 값 집합
     * @return 유효성 검사 결과
     */
    val validationFunc: (Set<String>) -> FieldTypeValidationResult
)