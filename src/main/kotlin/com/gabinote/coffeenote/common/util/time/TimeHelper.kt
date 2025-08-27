package com.gabinote.coffeenote.common.util.time

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object TimeHelper {
    fun isValidLocalDateTime(
        input: String,
        pattern: String = DateTimeFormatter.ISO_LOCAL_DATE_TIME.toString()
    ): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            LocalDateTime.parse(input, formatter)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    fun isValidTime(input: String, pattern: String = "HH:mm"): Boolean {
        return try {
            val fmt = DateTimeFormatter.ofPattern(pattern)
            LocalTime.parse(input, fmt)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }
}