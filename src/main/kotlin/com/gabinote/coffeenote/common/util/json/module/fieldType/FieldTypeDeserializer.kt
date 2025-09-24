package com.gabinote.coffeenote.common.util.json.module.fieldType

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeFactory
import org.springframework.boot.jackson.JsonComponent

@JsonComponent
class FieldTypeDeserializer(
    private val fieldTypeFactory: FieldTypeFactory
) : JsonDeserializer<FieldType>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): FieldType {
        val value = p.codec.readTree<JsonNode>(p).asText()

        return fieldTypeFactory.getFieldType(key = value)
            ?: ctxt.handleWeirdStringValue(
                FieldType::class.java,
                value,
                "Unknown field type key"
            ) as FieldType
    }

}