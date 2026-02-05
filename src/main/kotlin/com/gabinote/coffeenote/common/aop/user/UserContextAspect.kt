package com.gabinote.coffeenote.common.aop.user

import com.gabinote.coffeenote.common.util.context.UserContext
import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

private val logger = KotlinLogging.logger {}

/**
 * UserContext 초기화용 AOP 클래스
 * 모든 RestController에 대해 요청이 들어올 때마다 UserContext를 초기화 함
 * @see com.gabinote.coffeenote.common.util.context.UserContext
 */
@Order(1)
@Aspect
@Component
class UserContextAspect(
    private val userContext: UserContext,
) {
    /**
     * 모든 RestController 메서드 호출 전, UserContext를 초기화
     * HTTP 헤더에서 "X-Token-Sub"와 "X-Token-Roles" 값을 추출하여 UserContext에 설정
     * @param joinPoint AOP 조인 포인트
     */
    @Before("@within(org.springframework.web.bind.annotation.RestController)")
    fun initUserContext(joinPoint: JoinPoint) {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        // keycloak sub
        userContext.uid = request.getHeader("X-Token-Sub")

        // keycloak roles
        userContext.roles = request.getHeader("X-Token-Roles")?.split(",") ?: emptyList()

        logger.debug { "User context initialized with uid: ${userContext.uid} and roles: ${userContext.roles}" }
    }
}