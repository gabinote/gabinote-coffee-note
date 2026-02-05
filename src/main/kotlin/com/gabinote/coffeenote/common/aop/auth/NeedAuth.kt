package com.gabinote.coffeenote.common.aop.auth

import java.lang.annotation.Inherited

/**
 * 인증이 필요한 API 임을 나타내는 어노테이션
 * 해당 어노테이션이 붙은 메서드는 userContext의 isLoggedIn이 true여야만 접근 가능
 *
 * @see com.gabinote.coffeenote.common.aop.auth.NeedAuthAspect
 * @see com.gabinote.coffeenote.common.util.context.UserContext
 */
@Inherited
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NeedAuth