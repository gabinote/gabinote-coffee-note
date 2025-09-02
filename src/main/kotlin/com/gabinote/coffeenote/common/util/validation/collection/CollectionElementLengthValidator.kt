package com.gabinote.coffeenote.common.util.validation.collection

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class CollectionElementLengthValidator : ConstraintValidator<CollectionElementLength, Collection<String>> {
    private var length: Int = 0
    private var nullable: Boolean = false
    private var notEmpty: Boolean = false
    private var resourceName: String = "element"

    override fun initialize(constraintAnnotation: CollectionElementLength) {
        length = constraintAnnotation.length
        nullable = constraintAnnotation.nullable
        notEmpty = constraintAnnotation.notEmpty
        resourceName = constraintAnnotation.resourceName
    }

    override fun isValid(
        value: Collection<String>,
        context: ConstraintValidatorContext
    ): Boolean {
        value.forEach {
            if (!checkString(it, context)) {
                return false
            }
        }
        return true
    }

    private fun checkString(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value == null) {
            if (!nullable) {
                context.disableDefaultConstraintViolation()
                context.buildConstraintViolationWithTemplate(
                    "$resourceName is required and cannot be null."
                ).addConstraintViolation()
                return false
            }
            return true
        }

        if (value.isEmpty()) {
            if (!notEmpty) {
                context.disableDefaultConstraintViolation()
                context.buildConstraintViolationWithTemplate(
                    "$resourceName is required and cannot be empty."
                ).addConstraintViolation()
                return false
            }
            return true
        }

        if (value.length > length) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                "$resourceName length cannot exceed $length characters."
            ).addConstraintViolation()
            return false
        }
        return true
    }
}