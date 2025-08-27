package com.gabinote.coffeenote.common.util.validation.page.sort

import com.gabinote.coffeenote.common.domain.base.BaseSortKey
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [PageSortKeyValidator::class])
@Target(*[AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER])
@Retention(AnnotationRetention.RUNTIME)
annotation class PageSortKeyCheck(
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val sortKey: KClass<out BaseSortKey>,
    val message: String = "Page sort key not valid"
)
