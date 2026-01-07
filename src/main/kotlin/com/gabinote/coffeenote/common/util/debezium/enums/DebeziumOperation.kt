package com.gabinote.coffeenote.common.util.debezium.enums

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