package com.gabinote.coffeenote.field.domain.fieldType

/**
 * 짧은 텍스트 입력 필드 타입을 구현하는 싱글톤 객체
 * 최대 100자 길이의 간단한 텍스트 입력을 위한 필드
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
object ShortTextField : TextField() {
    /**
     * 짧은 텍스트 필드 타입의 고유 키
     */
    override val key: String
        get() = "SHORT_TEXT"

    /**
     * 짧은 텍스트 필드의 최대 길이 (100자)
     */
    override val maxLength: Int = 100

    /**
     * 유효성 검사 메시지에 사용되는 필드 타입 이름
     */
    override val messageTypeName: String = "Short Text"

}