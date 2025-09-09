package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.field.domain.attribute.Attribute
import java.util.Locale.getDefault

/**
 * 토글(참/거짓) 필드 타입을 구현하는 싱글톤 객체
 * Boolean 값을 표현하는 필드 타입
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
object ToggleField : FieldType() {
    /**
     * 토글 필드 타입의 고유 키
     */
    override val key: String
        get() = "TOGGLE"

    /**
     * 토글 필드가 지원하는 속성 키 집합
     */
    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf()

    /**
     * 토글 필드 값의 유효성 검사를 수행
     * @param values 검사할 값 집합 (true/false)
     * @param attributes 필드 속성 집합
     * @return 유효성 검사 결과 목록
     */
    override fun validationValues(values: Set<String>, attributes: Set<Attribute>): List<FieldTypeValidationResult> {
        val results = mutableListOf<FieldTypeValidationResult>()
        if (values.size != 1) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "TOGGLE field can has only 1 value"
                )
            )
        }

        val value = values.firstOrEmptyString().lowercase(getDefault())
        if (value != "true" && value != "false") {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "TOGGLE field value must be either 'true' or 'false'"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}