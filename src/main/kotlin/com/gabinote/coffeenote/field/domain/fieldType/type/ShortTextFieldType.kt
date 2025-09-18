package com.gabinote.coffeenote.field.domain.fieldType.type

import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeKey
import org.springframework.stereotype.Component

/**
 * 짧은 텍스트 입력 필드 타입을 구현하는 싱글톤 객체
 * 최대 100자 길이의 간단한 텍스트 입력을 위한 필드
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
@Component
class ShortTextFieldType : TextFieldType() {
    /**
     * 짧은 텍스트 필드 타입의 고유 키
     */
    override val key: FieldTypeKey = FieldTypeKey.SHORT_TEXT

    /**
     * 짧은 텍스트 필드가 리스트 보기에서 표시될 수 있는지 여부
     * true: 표시 가능, false: 표시 불가
     */
    override val canDisplay: Boolean = true

    /**
     * 짧은 텍스트 필드의 최대 길이 (100자)
     */
    override val maxLength: Int = 100

    /**
     * 유효성 검사 메시지에 사용되는 필드 타입 이름
     */
    override val messageTypeName: String = "Short Text"

}