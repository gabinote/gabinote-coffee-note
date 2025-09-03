package com.gabinote.coffeenote.common.util.collection

object CollectionHelper {
    fun Collection<*>.firstOrEmptyString(): String {
        return this.firstOrNull()?.toString() ?: ""
    }
}