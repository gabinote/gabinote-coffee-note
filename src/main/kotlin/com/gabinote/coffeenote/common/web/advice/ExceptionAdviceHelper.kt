package com.gabinote.coffeenote.common.web.advice

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import java.net.URI
import java.util.UUID

object ExceptionAdviceHelper {
    fun getRequestId(request: HttpServletRequest): String =
        request.getHeader("X-Request-Id")?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()

    fun problemDetail(
        status: HttpStatus,
        title: String? = "Unexpected Error",
        detail: String? = null,
        type: URI = URI("about:blank"),
        instance: URI? = null,
        requestId: String? = null,
        additionalProperties: Map<String, Any> = emptyMap()
    ): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, detail ?: title)
        problemDetail.title = title
        problemDetail.type = type
        problemDetail.instance = instance
        problemDetail.properties?.let { it["requestId"] = requestId ?: UUID.randomUUID().toString() }
        problemDetail.properties?.putAll(additionalProperties)
        return problemDetail
    }
}