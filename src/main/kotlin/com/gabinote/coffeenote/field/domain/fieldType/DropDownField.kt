package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.field.domain.attribute.Attribute

/**
 * 드롭다운 선택 필드 타입을 구현하는 싱글톤 객체
 * 단일 항목을 선택할 수 있는 드롭다운 목록 필드
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
object DropDownField : ListSelectField() {
    /**
     * 드롭다운 필드 타입의 고유 키
     */
    override val key: String = "DROP_DOWN"

    /**
     * 드롭다운 필드가 리스트 보기에서 표시될 수 있는지 여부
     * true: 표시 가능, false: 표시 불가
     */
    override val canDisplay: Boolean = true

    /**
     * 드롭다운 필드 값의 유효성 검사를 수행
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
                    message = "Dropdown field can has only 1 value"
                )
            )
        }

        val value = values.firstOrEmptyString()
        if (value.length > 50) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Dropdown field value cannot exceed 50 characters"
                )
            )
        }

        if (value.isEmpty()) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Dropdown field value cannot be empty"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        val allowAddValue = getAllowAddValue(attributes)
        val allowValues = getValues(attributes)

        if (value !in allowValues && !allowAddValue) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "The value is not in the list of allowed values, and adding new values is not permitted."
                )
            )
        }

        return results
    }

}