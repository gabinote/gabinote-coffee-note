package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.field.domain.attribute.Attribute

/**
 * FieldType을 나타내는 sealed class
 * 다양한 필드 타입들의 기본 클래스
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
sealed class FieldType {

    /**
     * FieldType의 고유 키
     * dto의 fieldType 필드와 매핑됨
     */
    abstract val key: String

    /**
     * FieldType이 리스트 보기에서 표시될 수 있는지 여부
     * true: 표시 가능, false: 표시 불가
     */
    abstract val canDisplay: Boolean

    /**
     * FieldType이 가질 수 있는 Attribute 키들
     * 각 필드 타입별로 지원하는 속성 키 집합
     */
    abstract val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey>

    /**
     * 필드 값들에 대한 유효성 검사 수행
     * @param values 검사할 값들의 집합
     * @param attributes 필드 속성 집합
     * @return 유효성 검사 결과 목록
     */
    abstract fun validationValues(values: Set<String>, attributes: Set<Attribute>): List<FieldTypeValidationResult>

    /**
     * 필드 속성들에 대한 유효성 검사 수행
     * @param attributes 검사할 속성 집합
     * @return 유효성 검사 결과 목록
     */
    fun validationAttributes(attributes: Set<Attribute>): List<FieldTypeValidationResult> {
        val attributeMap = attributes.associateBy { it.key }
        return fieldTypeAttributeKeys.map { fieldTypeAttributeKey ->
            validateAttribute(fieldTypeAttributeKey, attributeMap)
        }
    }

    /**
     * 개별 속성에 대한 유효성 검사 수행
     * @param fieldTypeAttributeKey 검사할 속성 키
     * @param attributeMap 속성 맵
     * @return 유효성 검사 결과
     */
    private fun validateAttribute(
        fieldTypeAttributeKey: FieldTypeAttributeKey,
        attributeMap: Map<String, Attribute>
    ): FieldTypeValidationResult {
        return attributeMap[fieldTypeAttributeKey.key]?.let { value ->
            fieldTypeAttributeKey.validationFunc(value.value)
        } ?: FieldTypeValidationResult(
            valid = false,
            message = "Unknown attribute key: ${fieldTypeAttributeKey.key}"
        )
    }
}