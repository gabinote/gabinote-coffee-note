package com.gabinote.coffeenote.field.domain.fieldType

/**
 * 긴 텍스트 입력 필드 타입을 구현하는 싱글톤 객체
 * 대용량 텍스트 입력을 위한 필드 타입
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
object LongTextField : TextField() {
    /**
     * 긴 텍스트 필드 타입의 고유 키
     */
    override val key: String
        get() = "LONG_TEXT"

    /**
     * 긴 텍스트 필드의 최대 길이 (10000자)
     */
    override val maxLength: Int = 10000

    /**
     * 유효성 검사 메시지에 사용되는 필드 타입 이름
     */
    override val messageTypeName: String = "Long Text"

}