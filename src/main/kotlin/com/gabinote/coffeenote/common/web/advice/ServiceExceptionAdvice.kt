package com.gabinote.coffeenote.common.web.advice

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.gabinote.coffeenote.common.util.exception.service.ResourceDuplicate
import com.gabinote.coffeenote.common.util.exception.service.ResourceNotFound
import com.gabinote.coffeenote.common.web.advice.ExceptionAdviceHelper.getRequestId
import com.gabinote.coffeenote.common.web.advice.ExceptionAdviceHelper.problemDetail
import com.gabinote.gateway.manager.api.common.web.advice.ErrorLog
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.net.URI
import java.util.*

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class ServiceExceptionAdvice {

    @ExceptionHandler(ResourceNotFound::class)
    fun handleResourceNotFound(
        ex: ResourceNotFound,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail> {
        val requestId = getRequestId(request)
        val status = HttpStatus.NOT_FOUND
        val problemDetail = problemDetail(
            status = status,
            title = "Resource Not Found",
            detail = ex.errorMessage,
            type = URI("https://httpstatuses.com/404"),
            requestId = requestId
        )
        val log = ErrorLog(
            requestId = requestId,
            method = request.method,
            path = request.requestURI,
            status = status,
            error = "ResourceNotFound",
            message = ex.errorMessage
        )

        logger.info { log.toString() }
        return ResponseEntity(problemDetail, status)
    }

    @ExceptionHandler(ResourceDuplicate::class)
    fun handleResourceDuplicate(
        ex: ResourceDuplicate,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail> {
        val requestId = getRequestId(request)
        val status = HttpStatus.CONFLICT
        val problemDetail = problemDetail(
            status = status,
            title = "Resource Duplicate",
            detail = ex.message,
            type = URI("https://httpstatuses.com/409"),
            requestId = requestId
        )
        val log = ErrorLog(
            requestId = requestId,
            method = request.method,
            path = request.requestURI,
            status = status,
            error = "ResourceDuplicate",
            message = ex.message
        )
        logger.info { log.toString() }
        return ResponseEntity(problemDetail, status)
    }
}