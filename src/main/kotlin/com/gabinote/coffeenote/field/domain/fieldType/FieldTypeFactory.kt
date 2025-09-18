package com.gabinote.coffeenote.field.domain.fieldType

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class FieldTypeFactory(
    private val fieldTypes: Set<FieldType>
) {
    private lateinit var fieldTypeMap: Map<FieldTypeKey, FieldType>

    @PostConstruct
    fun init() {
        this.fieldTypeMap = fieldTypes.associateBy { it.key }
    }

    fun getFieldType(key: FieldTypeKey): FieldType {
        return fieldTypeMap[key] ?: throw IllegalArgumentException("Invalid field type key: $key")
    }

    fun getFieldType(key: String): FieldType? {
        val fieldTypeKey = FieldTypeKey.from(key) ?: return null
        return getFieldType(fieldTypeKey)
    }
}