package com.gabinote.coffeenote.field.domain.fieldType.type

import com.gabinote.coffeenote.common.util.collection.CollectionHelper.firstOrEmptyString
import com.gabinote.coffeenote.common.util.type.TypeCheckHelper.isInt
import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeAttributeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeValidationResult
import org.springframework.stereotype.Component

/**
 * 점수 입력 필드 타입을 구현하는 싱글톤 객체
 * 최대 점수를 지정할 수 있는 정수 값 평가 필드
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
@Component
class ScoreFieldType : FieldType() {
    /**
     * 점수 필드 타입의 고유 키
     */
    override val key: FieldTypeKey = FieldTypeKey.SCORE

    /**
     * 점수 필드가 리스트 보기에서 표시될 수 있는지 여부
     * true: 표시 가능, false: 표시 불가
     */
    override val canDisplay: Boolean = true

    /**
     * 점수 필드가 지원하는 속성 키 집합
     * - maxScore: 최대 점수 값 (3-10 사이 정수)
     */
    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf(
        FieldTypeAttributeKey(
            key = "maxScore",
            validationFunc = { value ->

                val valueIsInt = isInt(value.firstOrEmptyString())

                when {
                    value.size != 1 -> FieldTypeValidationResult(
                        valid = false,
                        message = "maxScore must have exactly 1 value"
                    )

                    !valueIsInt -> FieldTypeValidationResult(
                        valid = false,
                        message = "maxScore value must be an integer"
                    )

                    valueIsInt && value.firstOrEmptyString().toInt() > 10 -> FieldTypeValidationResult(
                        valid = false,
                        message = "maxScore value cannot be greater than 10"
                    )

                    valueIsInt && value.firstOrEmptyString().toInt() < 3 -> FieldTypeValidationResult(
                        valid = false,
                        message = "maxScore value cannot be less than 3"
                    )

                    else -> FieldTypeValidationResult(valid = true)
                }
            }
        ),
    )

    /**
     * 점수 필드 값의 유효성 검사를 수행
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
        val valueIsInt = isInt(value)

        if (!valueIsInt) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Date field value must be an integer"
                )
            )
        }
        val maxScore = getMaxScore(attributes)
        if (valueIsInt && value.toInt() > maxScore) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Date field value cannot be greater than $maxScore"
                )
            )
        }

        if (valueIsInt && value.toInt() < 1) {
            results.add(
                FieldTypeValidationResult(
                    valid = false,
                    message = "Date field value cannot be less than 1"
                )
            )
        }

        if (results.isEmpty()) {
            results.add(FieldTypeValidationResult(valid = true))
        }

        return results
    }

    /**
     * 속성에서 최대 점수 값을 추출
     * @param attributes 필드 속성 집합
     * @return 최대 점수 값
     * @throws IllegalArgumentException 유효하지 않은 maxScore 속성 또는 값인 경우
     */
    private fun getMaxScore(attributes: Set<Attribute>): Int {
        val source = attributes.firstOrNull { it.key == "maxScore" }?.value

        if (source == null || source.size != 1) {
            throw IllegalArgumentException("Invalid maxScore attribute")
        }

        if (!isInt(source.first())) {
            throw IllegalArgumentException("Invalid maxScore attribute value")
        }

        return source.first().toInt()

    }
}