package com.gabinote.coffeenote.field.domain.fieldType.type

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeAttributeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeValidationResult

/**
 * 텍스트 입력 필드 타입의 기본 추상 클래스
 * 텍스트 기반 필드 타입들의 공통 기능 제공
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
abstract class TextFieldType : FieldType() {
    /**
     * 텍스트 필드의 최대 길이
     */
    open val maxLength: Int = 100

    /**
     * 유효성 검사 메시지에 사용되는 필드 타입 이름
     */
    open val messageTypeName: String = "Text"

    /**
     * 텍스트 필드가 지원하는 속성 키 집합
     */
    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf()

    /**
     * 텍스트 필드 값의 유효성 검사를 수행
     * @param values 검사할 값 집합
     * @param attributes 필드 속성 집합
     * @return 유효성 검사 결과 목록
     */
    override fun validationValues(values: Set<String>, attributes: Set<Attribute>): List<FieldTypeValidationResult> {
        val results = mutableListOf<FieldTypeValidationResult>()
        if (values.size != 1) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "$messageTypeName field can has only 1 value"
                )
            )
        }

        val value = values.firstOrEmptyString()

        if (value.length > maxLength) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "$messageTypeName field value cannot exceed $maxLength characters"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}