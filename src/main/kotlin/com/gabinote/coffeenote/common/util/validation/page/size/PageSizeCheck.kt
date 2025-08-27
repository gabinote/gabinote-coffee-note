package com.gabinote.coffeenote.common.util.validation.page.size

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [PageSizeValidator::class])
@Target(*[AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER])
@Retention(AnnotationRetention.RUNTIME)
annotation class PageSizeCheck(
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val max: Int = 0,
    val min: Int = 0,
    val message: String = "Page size not valid"
)
