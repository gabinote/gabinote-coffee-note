package com.gabinote.coffeenote.field.util.validation.fieldType

import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeRegistry
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component

@Component
class RequiredFieldTypeValidator(
    private val fieldTypeRegistry: FieldTypeRegistry,
) : ConstraintValidator<RequiredFieldType, String> {

    private var allowedNull: Boolean = false

    override fun initialize(constraintAnnotation: RequiredFieldType) {
        allowedNull = constraintAnnotation.allowedNull
    }

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