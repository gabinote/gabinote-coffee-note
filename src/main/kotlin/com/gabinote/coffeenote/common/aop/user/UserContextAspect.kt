package com.gabinote.coffeenote.common.aop.user

import com.gabinote.coffeenote.common.util.context.UserContext
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class UserContextAspect(
    private val userContext: UserContext
) {

    @Before("@within(org.springframework.web.bind.annotation.RestController)")
    fun initUserContext(joinPoint: JoinPoint) {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        userContext.uid = request.getHeader("X-Token-Sub")
        userContext.roles = request.getHeader("X-Token-Roles")?.split(",") ?: emptyList()
    }
}