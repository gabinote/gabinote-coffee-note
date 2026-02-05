package com.gabinote.coffeenote.common.aop.auth

import com.gabinote.coffeenote.common.util.context.UserContext
import com.gabinote.coffeenote.common.util.exception.controller.GatewayAuthFailed
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * NeedAuth 어노테이션 처리용 AOP 클래스
 * @see com.gabinote.coffeenote.common.aop.auth.NeedAuth
 * @see com.gabinote.coffeenote.common.util.context.UserContext
 */
@Order(2)
@Aspect
@Component
class NeedAuthAspect(
    private val userContext: UserContext,
) {
    /**
     * NeedAuth 어노테이션이 붙은 메서드 호출 전, UserContext의 isLoggedIn이 true인지 검사
     * 인증되지 않은 상태라면 GatewayAuthFailed 예외 발생
     *
     * @param joinPoint AOP 조인 포인트
     * @param needAuth NeedAuth 어노테이션 인스턴스
     *
     * @throws GatewayAuthFailed 인증되지 않은 상태일 때 발생
     */
    @Before("@annotation(needAuth)")
    fun checkUserLoggedIn(joinPoint: JoinPoint, needAuth: NeedAuth) {
        if (!userContext.isLoggedIn()) {
            // 정상적인 Gateway 인증을 통과했음에도 불구하고 인증 안된 상태라는것은 서버 에러
            throw GatewayAuthFailed()
        }
    }
}