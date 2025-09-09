package com.gabinote.coffeenote.field.util.validation.fieldType

import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeRegistry
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component

/**
 * 필드 타입 유효성을 검증하는 밸리데이터
 * RequiredFieldType 애노테이션에 대한 검증 로직 구현
 * @author 황준서
 */
@Component
class RequiredFieldTypeValidator(
    private val fieldTypeRegistry: FieldTypeRegistry,
) : ConstraintValidator<RequiredFieldType, String> {

    /**
     * null 값 허용 여부
     */
    private var allowedNull: Boolean = false

    /**
     * 밸리데이터 초기화
     * @param constraintAnnotation 검증 애노테이션
     */
    override fun initialize(constraintAnnotation: RequiredFieldType) {
        allowedNull = constraintAnnotation.allowedNull
    }

    /**
     * 필드 타입 유효성 검증
     * @param value 검증할 필드 타입 값
     * @param context 검증 컨텍스트
     * @return 유효성 여부
     */
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) {
            if (!allowedNull) {
                context.disableDefaultConstraintViolation()
                context.buildConstraintViolationWithTemplate(
                    "Field type is required and cannot be null."
                ).addConstraintViolation()
                return false
            }
            return true
        }
        if (!fieldTypeRegistry.isFieldType(value)) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                "Invalid field type: $value. Allowed types are: ${fieldTypeRegistry.allTypes().map { it.key }}"
            ).addConstraintViolation()
            return false
        }
        return true
    }
}