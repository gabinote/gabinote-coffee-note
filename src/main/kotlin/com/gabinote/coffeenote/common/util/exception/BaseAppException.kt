package com.gabinote.coffeenote.common.util.exception


abstract class BaseAppException(
) : RuntimeException() {
    abstract val errorMessage: String
    abstract val logMessage: String
}