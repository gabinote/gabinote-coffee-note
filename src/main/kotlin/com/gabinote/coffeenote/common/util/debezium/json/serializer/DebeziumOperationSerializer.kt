package com.gabinote.coffeenote.common.util.debezium.json.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.gabinote.coffeenote.common.util.debezium.enums.DebeziumOperation

/**
 * DebeziumOperation 열거형을 JSON 문자열로 직렬화하는 데 사용되는 커스텀 시리얼라이저
 * 예: DebeziumOperation.CREATE -> "c"
 * @see DebeziumOperation
 */
class DebeziumOperationSerializer : JsonSerializer<DebeziumOperation>() {
    override fun serialize(
        value: DebeziumOperation,
        gen: JsonGenerator,
        serializers: SerializerProvider,
    ) {
        gen.writeString(value.code)
    }
}