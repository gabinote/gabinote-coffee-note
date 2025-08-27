package com.gabinote.coffeenote.common.util.type

object TypeCheckHelper {
    fun isInt(value: String): Boolean {
        return value.toIntOrNull() != null
    }

    fun isDouble(value: String): Boolean {
        return value.toDoubleOrNull() != null
    }

    fun isBoolean(value: String): Boolean {
        return value.lowercase() == "true" || value.lowercase() == "false"
    }
}