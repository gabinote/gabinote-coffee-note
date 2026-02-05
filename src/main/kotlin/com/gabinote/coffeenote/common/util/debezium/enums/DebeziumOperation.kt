package com.gabinote.coffeenote.common.util.debezium.enums

/**
 * Debezium Change Data Capture(CDC)에서 사용되는 작업(Operation) 타입을 나타내는 열거형
 */
enum class DebeziumOperation(val code: String) {
    CREATE("c"),
    UPDATE("u"),
    DELETE("d"),
    READ("r");

    companion object {
        fun fromCode(code: String): DebeziumOperation {
            return entries.find { it.code == code }
                ?: throw IllegalArgumentException("Unknown operation code: $code")
        }
    }
}