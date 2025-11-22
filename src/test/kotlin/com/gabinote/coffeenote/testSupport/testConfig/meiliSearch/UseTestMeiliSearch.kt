package com.gabinote.coffeenote.testSupport.testConfig.meiliSearch

import com.gabinote.coffeenote.common.config.JacksonConfig
import com.gabinote.coffeenote.common.config.MeiliSearchConfig
import com.gabinote.coffeenote.common.util.meiliSearch.client.MeiliSearchClient
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import java.lang.annotation.Inherited

@Testcontainers
@Import(JacksonConfig::class, MeiliSearchConfig::class, MeiliSearchClient::class)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [MeiliSearchContainerInitializer::class])
annotation class UseTestMeiliSearch