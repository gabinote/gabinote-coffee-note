package com.gabinote.coffeenote.common.util.validation.string.blank

import jakarta.validation.ConstraintValidator

class NullableNotBlankValidator : ConstraintValidator<NullableNotBlank, String?> {
    private var resourceName: String = "element"

    override fun initialize(constraintAnnotation: NullableNotBlank) {
        resourceName = constraintAnnotation.resourceName
    }

    override fun isValid(value: String?, context: jakarta.validation.ConstraintValidatorContext): Boolean {
        if (value == null) {
            return true
        }
        if (value.isBlank()) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                "$resourceName must not be blank."
            ).addConstraintViolation()
            return false
        }
        return true
    }
}