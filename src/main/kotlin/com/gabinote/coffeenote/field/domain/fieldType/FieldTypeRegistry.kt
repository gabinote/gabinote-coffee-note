package com.gabinote.coffeenote.field.domain.fieldType

import org.springframework.stereotype.Component

@Component
class FieldTypeRegistry(
    private val fieldTypes: List<FieldType> = listOf(
        DateField,
        DropDownField,
        LongTextField,
        MultiSelectField,
        NumberField,
        ScoreField,
        TextField,
        TimeField,
        ToggleField,
    )
) {
    private val typeMap: Map<String, FieldType> = fieldTypes.associateBy { it.key }

    fun fromString(key: String): FieldType =
        typeMap[key] ?: throw IllegalArgumentException("Unknown field type key: $key")

    fun allTypes(): Collection<FieldType> = typeMap.values
}