package com.gabinote.coffeenote.note.util.validation.noteField

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [NoteFieldValidator::class])
annotation class ValidNoteField(
    val message: String = "Invalid note field format.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)