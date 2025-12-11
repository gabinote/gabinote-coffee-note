package com.gabinote.coffeenote.note.util.validation.noteField

data class NoteFieldValidationRes(
    val isValid: Boolean,
    val errorMessage: String? = null,
)