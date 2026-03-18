package com.gabinote.coffeenote.testSupport.testConfig.keycloak

import com.gabinote.ums.testSupport.testConfig.keycloak.KeycloakContainerInitializer
import org.springframework.test.context.ContextConfiguration
import java.lang.annotation.Inherited


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@ContextConfiguration(initializers = [KeycloakContainerInitializer::class])
annotation class UseTestKeycloak