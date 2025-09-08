package com.gabinote.coffeenote.field.domain.fieldType

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.common.util.time.TimeHelper
import com.gabinote.coffeenote.field.domain.attribute.Attribute

/**
 * 시간 선택 필드 타입을 구현하는 싱글톤 객체
 * 시간 값(HH:mm)을 처리하며 12/24시간제 표시 옵션 지원
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
object TimeField : FieldType() {
    /**
     * 시간 필드 타입의 고유 키
     */
    override val key: String
        get() = "TIME"

    /**
     * 시간 필드가 지원하는 속성 키 집합
     */
    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf(
        // 표시 형식을 12시간제 또는 24시간제로 설정하는 속성
        // "true" (24시간제) 또는 "false" (12시간제) 값을 가짐
        // 저장은 HH:mm 형식으로 저장되며, 표시 형식에 따라 변환하여 보여줌
        FieldTypeAttributeKey(
            key = "24Format",
            validationFunc = { value ->
                when {
                    value.size != 1 -> FieldTypeValidationResult(
                        valid = false,
                        message = "24Format must have exactly 1 value"
                    )

                    value.firstOrEmptyString() !in setOf("true", "false") -> FieldTypeValidationResult(
                        valid = false,
                        message = "24Format value must be either 'true' or 'false'"
                    )

                    else -> FieldTypeValidationResult(valid = true)
                }
            }
        ),
    )

    /**
     * 시간 필드 값의 유효성 검사를 수행
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
                    message = "Time field can has only 1 value"
                )
            )
        }

        val value = values.firstOrEmptyString()
        // 저장은 HH:mm 형식으로 저장되며, 표시 형식에 따라 변환하여 보여줌
        if (!TimeHelper.isValidTime(value)) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Time field value must be in HH:mm format (e.g., 14:48)"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}