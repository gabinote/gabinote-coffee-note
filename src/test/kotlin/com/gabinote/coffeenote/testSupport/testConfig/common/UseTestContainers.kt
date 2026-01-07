package com.gabinote.coffeenote.testSupport.testConfig.common

import com.gabinote.coffeenote.testSupport.testConfig.db.DatabaseContainerInitializer
import com.gabinote.coffeenote.testSupport.testConfig.debezium.DebeziumContainerInitializer
import com.gabinote.coffeenote.testSupport.testConfig.meiliSearch.MeiliSearchContainerInitializer
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.lang.annotation.Inherited

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [DatabaseContainerInitializer::class, MeiliSearchContainerInitializer::class, DebeziumContainerInitializer::class])
annotation class UseTestContainers