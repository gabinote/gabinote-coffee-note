package com.gabinote.coffeenote.common.util.json.fieldType

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import org.springframework.stereotype.Component

@Component
class FieldTypeSerializer : JsonSerializer<FieldType>() {
    override fun serialize(
        value: FieldType,
        gen: JsonGenerator,
        serializers: SerializerProvider
    ) {
        gen.writeString(value.getKeyString())
    }
}