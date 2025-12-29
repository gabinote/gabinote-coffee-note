package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.field.domain.attribute.Attribute

abstract class FieldType {
    abstract val key: FieldTypeKey

    /**
     * FieldType이 리스트 보기에서 표시될 수 있는지 여부
     * true: 표시 가능, false: 표시 불가
     */
    abstract val canDisplay: Boolean

    abstract val isExcludeIndexing: Boolean

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
        val errorResult = mutableListOf<FieldTypeValidationResult>()
        validateAttributeNotDuplicated(attributes = attributes, errors = errorResult)
        val attributeMap = attributes.associateBy { it.key }
        fieldTypeAttributeKeys.map { fieldTypeAttributeKey ->
            validateAttribute(
                fieldTypeAttributeKey = fieldTypeAttributeKey,
                attributeMap = attributeMap,
                errors = errorResult
            )
        }
        return errorResult
    }

    private fun validateAttributeNotDuplicated(
        attributes: Set<Attribute>,
        errors: MutableList<FieldTypeValidationResult>,
    ) {
        val attributeKeys = attributes.map { it.key }
        val duplicatedKeys = attributeKeys.groupingBy { it }.eachCount().filter { it.value > 1 }.keys
        if (duplicatedKeys.isNotEmpty()) {
            errors.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Attribute keys are duplicated: ${duplicatedKeys.joinToString(", ")}"
                )
            )
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
        attributeMap: Map<String, Attribute>,
        errors: MutableList<FieldTypeValidationResult>,
    ) {

        attributeMap[fieldTypeAttributeKey.key]?.let { value ->
            val res = fieldTypeAttributeKey.validationFunc(value.value)
            if (!res.valid) {
                errors.add(res)
            }
        } ?: errors.add(
            FieldTypeValidationResult(
                valid = false,
                message = "Unknown attribute key: ${fieldTypeAttributeKey.key}"
            )
        )
    }

    fun getKeyString(): String {
        return key.key
    }
}