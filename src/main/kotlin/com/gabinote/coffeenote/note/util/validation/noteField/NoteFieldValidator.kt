package com.gabinote.coffeenote.note.util.validation.noteField

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class NoteFieldValidator(
    private val noteFieldValidationHelper: NoteFieldValidationHelper,
) : ConstraintValidator<ValidNoteField, Map<String, List<String>>> {


    override fun isValid(
        value: Map<String, List<String>>,
        context: ConstraintValidatorContext,
    ): Boolean {
        //1. key 검증
        val keys = value.keys
        noteFieldValidationHelper.validationKeys(keys)?.let {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(it.errorMessage!!)
                .addConstraintViolation()
            return false
        }

        //2. value 검증
        val allValues = value.values.flatten()
        noteFieldValidationHelper.validationValues(allValues)?.let {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(it.errorMessage!!)
                .addConstraintViolation()
            return false
        }

        return true
    }


}