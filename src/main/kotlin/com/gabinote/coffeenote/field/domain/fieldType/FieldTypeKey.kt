package com.gabinote.coffeenote.field.domain.fieldType

enum class FieldTypeKey(
    val key: String,
) {
    DROP_DOWN("DROP_DOWN"),
    DATE("DATE"),
    IMAGE("IMAGE"),
    LONG_TEXT("LONG_TEXT"),
    MULTI_SELECT("MULTI_SELECT"),
    NUMBER("NUMBER"),
    SCORE("SCORE"),
    SHORT_TEXT("SHORT_TEXT"),
    TIME("TIME"),
    TOGGLE("TOGGLE");

    companion object {
        fun from(key: String): FieldTypeKey? {
            return entries.find { it.key == key }
        }
    }
}