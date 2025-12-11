package com.gabinote.coffeenote.note.util.validation.noteField

object NoteFieldValidationResHelper {
    fun List<NoteFieldValidationRes>.isValid(): Boolean {
        return this.all { it.isValid }
    }

}