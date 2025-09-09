package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.field.domain.attribute.Attribute

/**
 * 이미지 필드 타입을 구현하는 싱글톤 객체
 * UUID 형식으로 저장된 이미지 참조를 관리하는 필드
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
object ImageField : FieldType() {
    /**
     * 이미지 필드 타입의 고유 키
     */
    override val key: String = "IMAGE"

    /**
     * 이미지 필드가 리스트 보기에서 표시될 수 있는지 여부
     * true: 표시 가능, false: 표시 불가
     */
    override val canDisplay: Boolean = false

    /**
     * 이미지 필드가 지원하는 속성 키 집합
     */
    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf()

    /**
     * 이미지 필드 값의 유효성 검사를 수행
     * @param values 검사할 값 집합 (UUID 형식의 이미지 식별자)
     * @param attributes 필드 속성 집합
     * @return 유효성 검사 결과 목록
     */
    override fun validationValues(values: Set<String>, attributes: Set<Attribute>): List<FieldTypeValidationResult> {
        val results = mutableListOf<FieldTypeValidationResult>()
        if (values.size != 1) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Image field can has only 1 value"
                )
            )
        }

        val value = values.firstOrEmptyString()

        // match with uuid regex
        val uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$".toRegex()
        if (!value.matches(uuidRegex)) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Image field value must be a valid UUID"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}