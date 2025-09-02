package com.gabinote.coffeenote.common.aop.auth

import com.gabinote.coffeenote.common.util.context.UserContext
import com.gabinote.coffeenote.common.util.exception.controller.GatewayAuthFailed
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component


@Aspect
@Component
class NeedAuthAspect(
    private val userContext: UserContext
) {
    @Before("@annotation(needAuth)")
    fun checkUserLoggedIn(joinPoint: JoinPoint, needAuth: NeedAuth) {
        if (!userContext.isLoggedIn()) {
            throw GatewayAuthFailed()
        }
    }
}