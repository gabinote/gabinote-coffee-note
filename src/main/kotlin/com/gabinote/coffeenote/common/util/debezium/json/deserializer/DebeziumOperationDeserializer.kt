package com.gabinote.coffeenote.common.util.debezium.json.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.gabinote.coffeenote.common.util.debezium.enums.DebeziumOperation

class DebeziumOperationDeserializer : JsonDeserializer<DebeziumOperation>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): DebeziumOperation {
        return DebeziumOperation.fromCode(p.text)
    }
}