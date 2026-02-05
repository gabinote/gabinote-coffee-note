package com.gabinote.coffeenote.note.util.validation.noteField

import com.gabinote.coffeenote.note.dto.noteField.constraint.NoteFieldConstraints
import org.springframework.stereotype.Component

@Component
class NoteFieldValidationHelper {


    fun validationKeys(keys: Set<String>): NoteFieldValidationRes? {
        keys.forEach {
            val res = validationPerKey(it)
            if (res != null) {
                return res
            }
        }
        return null
    }

    fun validationValues(values: List<String>): NoteFieldValidationRes? {
        values.forEach { valueList ->
            val res = validationPerKey(valueList)
            if (res != null) {
                return res
            }
        }
        return null
    }

    private fun validationPerKey(key: String): NoteFieldValidationRes? {
        if (key.matches(NoteFieldConstraints.fieldNameRegex)) {
            return NoteFieldValidationRes(isValid = false, errorMessage = "Key '$key' contains invalid characters.")
        }

        if (key.length > NoteFieldConstraints.FIELD_NAME_MAX_LENGTH) {
            return NoteFieldValidationRes(
                isValid = false,
                errorMessage = "Key '$key' exceeds maximum length of ${NoteFieldConstraints.FIELD_NAME_MAX_LENGTH}."
            )
        }

        if (key.isBlank()) {
            return NoteFieldValidationRes(isValid = false, errorMessage = "Key cannot be blank.")
        }

        return null
    }

    private fun validationPerValue(value: String): NoteFieldValidationRes? {
        if (value.isBlank()) {
            return NoteFieldValidationRes(isValid = false, errorMessage = "Value cannot be blank.")
        }

        if (value.length > NoteFieldConstraints.FIELD_NAME_MAX_LENGTH) {
            return NoteFieldValidationRes(
                isValid = false,
                errorMessage = "Value '$value' exceeds maximum length of ${NoteFieldConstraints.FIELD_NAME_MAX_LENGTH}."
            )
        }
        return null
    }
}