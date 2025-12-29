package com.gabinote.coffeenote.field.domain.fieldType.type

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeAttributeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeValidationResult
import org.springframework.stereotype.Component
import java.util.Locale.getDefault

/**
 * 토글(참/거짓) 필드 타입을 구현하는 싱글톤 객체
 * Boolean 값을 표현하는 필드 타입
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
@Component
class ToggleFieldType : FieldType() {
    /**
     * 토글 필드 타입의 고유 키
     */
    override val key: FieldTypeKey = FieldTypeKey.TOGGLE

    /**
     * 토글 필드가 리스트 보기에서 표시될 수 있는지 여부
     * true: 표시 가능, false: 표시 불가
     */
    override val canDisplay: Boolean = true

    /**
     * 인덱싱에서 제외할지 여부
     */
    override val isExcludeIndexing: Boolean = true

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