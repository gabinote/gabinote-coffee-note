package com.gabinote.coffeenote.common.util.validation.collection

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [CollectionElementLengthValidator::class])
annotation class CollectionElementLength(
    val length: Int,
    val nullable: Boolean = false,
    val notEmpty: Boolean = false,
    val resourceName: String = "element",
    val groups: Array<KClass<*>> = [],
    val message: String = "Each element's length must not exceed {length} characters.",
    val payload: Array<KClass<out Payload>> = []
)
