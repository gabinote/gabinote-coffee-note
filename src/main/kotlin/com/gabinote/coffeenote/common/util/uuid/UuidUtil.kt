package com.gabinote.coffeenote.common.util.uuid

import java.util.*

object UuidUtil {
    fun String.toUuid(): UUID {
        return UUID.fromString(this)
    }
}