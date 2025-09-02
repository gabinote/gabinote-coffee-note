package com.gabinote.coffeenote.common.util.validation.string.blank

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [NullableNotBlankValidator::class])
annotation class NullableNotBlank(
    val resourceName: String = "element",
    val groups: Array<KClass<*>> = [],
    val message: String = "Each resourceName must not be blank",
    val payload: Array<KClass<out Payload>> = []
)
