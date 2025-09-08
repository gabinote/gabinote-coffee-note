package com.gabinote.coffeenote.field.domain.fieldType

/**
 * 필드 타입 유효성 검사 결과를 나타내는 데이터 클래스
 * 유효성 검사의 성공/실패 여부와 메시지를 포함
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
data class FieldTypeValidationResult(
    /**
     * 유효성 검사 성공 여부
     */
    val valid: Boolean,

    /**
     * 유효성 검사 결과 메시지
     * 실패 시 오류 내용, 성공 시 null 가능
     */
    val message: String? = null
)