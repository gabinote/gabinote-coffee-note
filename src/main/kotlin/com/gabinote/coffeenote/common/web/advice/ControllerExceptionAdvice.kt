package com.gabinote.coffeenote.common.web.advice

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.annotation.Order
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

@Order(1)
@RestControllerAdvice
class ControllerExceptionAdvice