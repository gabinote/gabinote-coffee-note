package com.gabinote.coffeenote.field.util.validation.fieldType

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [RequiredFieldTypeValidator::class])
annotation class RequiredFieldType(
    val allowedNull: Boolean = false,
    val groups: Array<KClass<*>> = [],
    val message: String = "Field type not valid",
    val payload: Array<KClass<out Payload>> = []
)
