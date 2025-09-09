package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.field.domain.attribute.Attribute

/**
 * 숫자 입력 필드 타입을 구현하는 싱글톤 객체
 * 단위 속성을 지원하는 숫자 값을 처리
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
object NumberField : FieldType() {
    /**
     * 숫자 필드 타입의 고유 키
     */
    override val key: String = "NUMBER"

    /**
     * 숫자 필드가 리스트 보기에서 표시될 수 있는지 여부
     * true: 표시 가능, false: 표시 불가
     */
    override val canDisplay: Boolean = true
    
    /**
     * 숫자 필드가 지원하는 속성 키 집합
     * - unit: 숫자 값의 단위 (예: kg, cm 등)
     */
    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf(
        FieldTypeAttributeKey(
            key = "unit",
            validationFunc = { value ->
                when {
                    value.size != 1 -> FieldTypeValidationResult(
                        valid = false,
                        message = "Unit must be a single non-empty string"
                    )

                    value.firstOrEmptyString().length > 50 -> FieldTypeValidationResult(
                        valid = false,
                        message = "Unit must be at most 10 characters long"
                    )

                    value.firstOrEmptyString().isBlank() -> FieldTypeValidationResult(
                        valid = false,
                        message = "Unit must not be empty or blank"
                    )

                    else -> FieldTypeValidationResult(valid = true)
                }
            }
        )
    )

    /**
     * 숫자 필드 값의 유효성 검사를 수행
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
                    message = "Number field can has only 1 value"
                )
            )
        }

        val value = values.firstOrEmptyString()
        if (value.length > 50) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Number field value cannot exceed 50 characters"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}