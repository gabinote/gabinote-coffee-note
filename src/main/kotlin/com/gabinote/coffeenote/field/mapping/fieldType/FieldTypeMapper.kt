package com.gabinote.coffeenote.field.mapping.fieldType

import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeFactory
import org.mapstruct.Mapper
import org.springframework.beans.factory.annotation.Autowired

@Mapper(
    componentModel = "spring"
)
abstract class FieldTypeMapper(
) {
    @Autowired
    protected lateinit var fieldTypeFactory: FieldTypeFactory

    fun toString(fieldType: FieldType): String {
        return fieldType.getKeyString()
    }

    fun toFieldType(key: String): FieldType {
        return fieldTypeFactory.getFieldType(key) ?: throw IllegalArgumentException("Invalid FieldType key: $key")
    }
}