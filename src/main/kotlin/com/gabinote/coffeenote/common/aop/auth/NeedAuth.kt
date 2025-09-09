package com.gabinote.coffeenote.common.aop.auth

import java.lang.annotation.Inherited

@Inherited
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NeedAuth