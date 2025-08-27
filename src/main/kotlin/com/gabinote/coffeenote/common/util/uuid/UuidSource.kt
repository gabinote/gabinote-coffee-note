package com.gabinote.coffeenote.common.util.uuid

import java.util.*


interface UuidSource {
    fun generateUuid(): UUID
}