package com.gabinote.coffeenote.common.filter

import io.kotest.core.spec.style.FeatureSpec
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.extension.ExtendWith

import org.springframework.test.annotation.DirtiesContext

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockKExtension::class)
class GatewayCertFilterTest : FeatureSpec() {
    private val secret = "test-secret"
    private val filter = GatewayCertFilter(gatewaySecret = secret)

    init {
        feature("[common] GatewayCertFilter Test") {

            feature("필터 적용 대상 path의 경우 (/api/로 시작하는 경우)") {
                scenario("올바른 시크릿이 헤더에 포함된 경우 필터를 통과한다") {
                    val validReq = mockk<HttpServletRequest>(relaxed = true)
                    val res = mockk<HttpServletResponse>(relaxed = true)
                    val chain = mockk<FilterChain>(relaxed = true)

                    every { validReq.getHeader("X-Gateway-Secret") } returns secret
                    every { validReq.servletPath } returns "/api/resource"
                    every { validReq.getAttribute(any()) } returns null
                    filter.doFilter(validReq, res, chain)

                    verify(exactly = 1) {
                        validReq.getHeader("X-Gateway-Secret")
                        validReq.servletPath
                    }

                    verify(exactly = 1) { chain.doFilter(any(), any()) }
                }

                scenario("잘못된 시크릿이 헤더에 포함된 경우 필터에서 차단한다") {
                    val invalidReq = mockk<HttpServletRequest>(relaxed = true)
                    val res = mockk<HttpServletResponse>(relaxed = true)
                    val chain = mockk<FilterChain>(relaxed = true)

                    every { invalidReq.getHeader("X-Gateway-Secret") } returns "invalid-secret"
                    every { invalidReq.getAttribute(any()) } returns null
                    every { invalidReq.servletPath } returns "/api/resource"
                    every { res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied") } returns Unit

                    filter.doFilter(invalidReq, res, chain)

                    verify(exactly = 1) {
                        invalidReq.getHeader("X-Gateway-Secret")
                        invalidReq.servletPath
                    }
                    verify(exactly = 1) { res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied") }
                    verify(exactly = 0) { chain.doFilter(any(), any()) }
                }

                scenario("헤더를 찾을 수 없으면, 필터에서 차단한다") {
                    val missingReq = mockk<HttpServletRequest>(relaxed = true)
                    val res = mockk<HttpServletResponse>(relaxed = true)
                    val chain = mockk<FilterChain>(relaxed = true)

                    every { missingReq.getHeader("X-Gateway-Secret") } returns null
                    every { missingReq.getAttribute(any()) } returns null
                    every { missingReq.servletPath } returns "/api/resource"
                    every { res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied") } returns Unit

                    filter.doFilter(missingReq, res, chain)

                    verify(exactly = 1) {
                        missingReq.getHeader("X-Gateway-Secret")
                        missingReq.servletPath
                    }
                    verify(exactly = 1) { res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied") }
                    verify(exactly = 0) { chain.doFilter(any(), any()) }
                }
            }

            feature("필터 비적용 대상 path의 경우 ( /api/로 시작하지 않는 경우)") {
                scenario("올바르지 않은 시크릿이 헤더에 포함된 경우에도 필터를 통과한다") {
                    val req = mockk<HttpServletRequest>(relaxed = true)
                    val res = mockk<HttpServletResponse>(relaxed = true)
                    val chain = mockk<FilterChain>(relaxed = true)

                    every { req.servletPath } returns "/public/resource"
                    every { req.getHeader("X-Gateway-Secret") } returns "invalid-secret"

                    filter.doFilter(req, res, chain)

                    verify(exactly = 0) { req.getHeader("X-Gateway-Secret") }
                    verify(exactly = 1) { chain.doFilter(any(), any()) }
                }

                scenario("헤더를 찾을 수 없어도, 필터를 통과한다") {
                    val req = mockk<HttpServletRequest>(relaxed = true)
                    val res = mockk<HttpServletResponse>(relaxed = true)
                    val chain = mockk<FilterChain>(relaxed = true)

                    every { req.servletPath } returns "/public/resource"
                    every { req.getHeader("X-Gateway-Secret") } returns null

                    filter.doFilter(req, res, chain)

                    verify(exactly = 0) { req.getHeader("X-Gateway-Secret") }
                    verify(exactly = 1) { chain.doFilter(any(), any()) }
                }

                scenario("올바른 시크릿이 헤더에 포함된 경우 필터를 통과한다") {
                    val req = mockk<HttpServletRequest>(relaxed = true)
                    val res = mockk<HttpServletResponse>(relaxed = true)
                    val chain = mockk<FilterChain>(relaxed = true)

                    every { req.servletPath } returns "/public/resource"
                    every { req.getHeader("X-Gateway-Secret") } returns secret

                    filter.doFilter(req, res, chain)

                    verify(exactly = 0) { req.getHeader("X-Gateway-Secret") }
                    verify(exactly = 1) { chain.doFilter(any(), any()) }
                }
            }
        }
    }
}