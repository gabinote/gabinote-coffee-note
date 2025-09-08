package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.field.domain.attribute.Attribute

/**
 * 다중 선택 필드 타입을 구현하는 싱글톤 객체
 * 여러 항목을 선택할 수 있는 필드 타입
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
object MultiSelectField : ListSelectField() {
    /**
     * 다중 선택 필드 타입의 고유 키
     */
    override val key: String
        get() = "MULTI_SELECT"

    /**
     * 다중 선택 필드 값의 유효성 검사를 수행
     * @param values 검사할 값 집합
     * @param attributes 필드 속성 집합
     * @return 유효성 검사 결과 목록
     */
    override fun validationValues(values: Set<String>, attributes: Set<Attribute>): List<FieldTypeValidationResult> {
        val results = mutableListOf<FieldTypeValidationResult>()
        if (values.size > 30) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Multi Select field can have at most 30 values"
                )
            )
        }

        if (values.any { it.length > 50 }) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Multi Select field value cannot exceed 50 characters"
                )
            )
        }

        if (values.any { it.isBlank() }) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Multi Select field value cannot be empty"
                )
            )
        }

        val allowAddValue = getAllowAddValue(attributes)
        val allowValues = getValues(attributes)

        if (!allowAddValue && values.any { it !in allowValues }) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "The value is not in the list of allowed values, and adding new values is not permitted."
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}