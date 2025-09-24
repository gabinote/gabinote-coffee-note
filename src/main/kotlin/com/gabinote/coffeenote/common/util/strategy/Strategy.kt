package com.gabinote.coffeenote.common.util.strategy

interface Strategy<T : Enum<T>> {
    val type: T
}