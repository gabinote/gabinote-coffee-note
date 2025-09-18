package com.gabinote.coffeenote.field.domain.fieldType.type

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.common.util.time.TimeHelper
import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeAttributeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeValidationResult
import org.springframework.stereotype.Component

/**
 * 날짜 선택 필드 타입을 구현하는 싱글톤 객체
 * ISO 로컬 날짜 형식(yyyy-MM-dd)의 값을 처리
 * @author 황준서
 */
@Component
class DateFieldType : FieldType() {
    /**
     * 날짜 필드 타입의 고유 키
     */
    override val key: FieldTypeKey = FieldTypeKey.DATE

    /**
     * 날짜 필드가 리스트 보기에서 표시될 수 있는지 여부
     * true: 표시 가능, false: 표시 불가
     */
    override val canDisplay: Boolean = true

    /**
     * 날짜 필드가 지원하는 속성 키 집합
     */
    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf()

    /**
     * 날짜 필드 값의 유효성 검사를 수행
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
                    message = "Date field can has only 1 value"
                )
            )
        }

        val value = values.firstOrEmptyString()
        if (!TimeHelper.isValidLocalDate(input = value)) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Date field value must be in ISO_LOCAL_DATE format (yyyy-MM-dd)"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

}