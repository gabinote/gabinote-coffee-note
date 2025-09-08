package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.field.domain.attribute.Attribute

/**
 * 목록 선택 필드 타입들의 기본 추상 클래스
 * 드롭다운, 다중 선택 등 목록에서 선택하는 필드들의 공통 기능 제공
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
abstract class ListSelectField : FieldType() {

    /**
     * 목록 선택 필드가 지원하는 속성 키 집합
     * - values: 선택 가능한 옵션 목록
     * - allowAddValue: 사용자 정의 값 추가 허용 여부
     */
    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf(
        FieldTypeAttributeKey(
            key = "values",
            validationFunc = { value ->
                when {
                    value.size < 2 -> FieldTypeValidationResult(
                        valid = false,
                        message = "At least two options are required. if you want to have only one option, use a Toggle field instead."
                    )

                    value.size > 100 -> FieldTypeValidationResult(
                        valid = false,
                        message = "Maximum number of options is 100."
                    )

                    value.any { it.isEmpty() } -> FieldTypeValidationResult(
                        valid = false,
                        message = "Options cannot be empty."
                    )

                    value.any { it.length > 50 } -> FieldTypeValidationResult(
                        valid = false,
                        message = "Each option cannot exceed 50 characters."
                    )

                    // 중복된 값이 있는지 검사
                    value.toSet().size != value.size -> FieldTypeValidationResult(
                        valid = false,
                        message = "Options cannot have duplicate values."
                    )

                    else -> FieldTypeValidationResult(valid = true)
                }
            }
        ),
        FieldTypeAttributeKey(
            key = "allowAddValue",
            validationFunc = { value ->
                when {
                    value.size != 1 -> FieldTypeValidationResult(
                        valid = false,
                        message = "AllowAddValue must be a single string"
                    )

                    value.firstOrEmptyString() !in setOf("true", "false") -> FieldTypeValidationResult(
                        valid = false,
                        message = "AllowAddValue must be either 'true' or 'false'"
                    )

                    else -> FieldTypeValidationResult(valid = true)
                }
            }
        )
    )

    /**
     * 사용자 정의 값 추가 허용 여부를 속성에서 추출
     * @param attributes 필드 속성 집합
     * @return 사용자 정의 값 추가 허용 여부
     * @throws IllegalArgumentException 유효하지 않은 allowAddValue 속성인 경우
     */
    fun getAllowAddValue(attributes: Set<Attribute>): Boolean {
        val source = attributes.find { it.key == "allowAddValue" }
        if (source == null || source.value.size != 1) {
            throw IllegalArgumentException("Invalid allowAddValue attribute")
        }
        return source.value.first() == "true"
    }

    /**
     * 선택 가능한 값 목록을 속성에서 추출
     * @param attributes 필드 속성 집합
     * @return 선택 가능한 값 집합
     * @throws IllegalArgumentException 유효하지 않은 values 속성인 경우
     */
    fun getValues(attributes: Set<Attribute>): Set<String> {
        val source = attributes.find { it.key == "values" }
        if (source == null || source.value.size < 2) {
            throw IllegalArgumentException("Invalid values attribute")
        }
        return source.value
    }

}