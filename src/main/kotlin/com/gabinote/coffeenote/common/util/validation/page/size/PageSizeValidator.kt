package com.gabinote.coffeenote.common.util.validation.page.size

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.data.domain.Pageable

class PageSizeValidator : ConstraintValidator<PageSizeCheck, Pageable> {

    private var max: Int = 0
    private var min: Int = 0

    override fun initialize(constraintAnnotation: PageSizeCheck) {
        max = constraintAnnotation.max
        min = constraintAnnotation.min
    }

    override fun isValid(value: Pageable, context: ConstraintValidatorContext): Boolean {
        if (value.pageSize in min..max) {
            return true
        }
        context.disableDefaultConstraintViolation()
        context.buildConstraintViolationWithTemplate("Page size must be between $min and $max. but was ${value.pageSize}")
            .addConstraintViolation()

        return false
    }


}