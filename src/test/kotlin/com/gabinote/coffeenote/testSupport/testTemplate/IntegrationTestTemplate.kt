package com.gabinote.coffeenote.testSupport.testTemplate

import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.testSupport.testConfig.common.UseTestContainers
import com.gabinote.coffeenote.testSupport.testUtil.data.TestDataHelper
import com.gabinote.coffeenote.testSupport.testUtil.uuid.TestUuidSource
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.test.TestCaseOrder
import io.restassured.RestAssured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.testcontainers.junit.jupiter.Testcontainers


@DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
@Import(
//    JacksonConfig::class,
    TestDataHelper::class,
    TestUuidSource::class,
)
@Testcontainers
@UseTestContainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class IntegrationTestTemplate : FeatureSpec() {
    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var testDataHelper: TestDataHelper


    val apiPrefix: String = "/api/v1"


    fun beforeSpec() {
        RestAssured.basePath = apiPrefix
        RestAssured.port = port
    }

    override fun testCaseOrder(): TestCaseOrder = TestCaseOrder.Random

    init {
        beforeSpec {
            beforeSpec()
        }
    }
}