package com.gabinote.coffeenote.common.util.debezium.json.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.gabinote.coffeenote.common.util.debezium.enums.DebeziumOperation

class DebeziumOperationSerializer : JsonSerializer<DebeziumOperation>() {
    override fun serialize(
        value: DebeziumOperation,
        gen: JsonGenerator,
        serializers: SerializerProvider,
    ) {
        gen.writeString(value.code)
    }
}