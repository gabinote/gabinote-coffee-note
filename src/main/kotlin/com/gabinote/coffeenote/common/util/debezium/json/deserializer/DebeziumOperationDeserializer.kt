package com.gabinote.coffeenote.common.util.debezium.json.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.gabinote.coffeenote.common.util.debezium.enums.DebeziumOperation

/**
 * DebeziumOperation 열거형을 JSON 문자열에서 역직렬화하는 데 사용되는 커스텀 디시리얼라이저
 * 예: "c" -> DebeziumOperation.CREATE
 * @see DebeziumOperation
 */
class DebeziumOperationDeserializer : JsonDeserializer<DebeziumOperation>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): DebeziumOperation {
        return DebeziumOperation.fromCode(p.text)
    }
}