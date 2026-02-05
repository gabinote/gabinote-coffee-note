package com.gabinote.coffeenote.testSupport.testTemplate

import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.testSupport.testConfig.common.UseTestContainers
import com.gabinote.coffeenote.testSupport.testUtil.database.TestDataHelper
import com.gabinote.coffeenote.testSupport.testUtil.debezium.TestDebeziumHelper
import com.gabinote.coffeenote.testSupport.testUtil.kafka.TestKafkaHelper
import com.gabinote.coffeenote.testSupport.testUtil.meilisearch.TestMeiliSearchHelper
import com.gabinote.coffeenote.testSupport.testUtil.time.TestTimeProvider
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
    TestDataHelper::class,
    TestUuidSource::class,
    TestMeiliSearchHelper::class,
    TestTimeProvider::class,
    TestKafkaHelper::class,
    TestDebeziumHelper::class,
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

    @Autowired
    lateinit var testMeiliSearchHelper: TestMeiliSearchHelper

    @Autowired
    lateinit var testKafkaHelper: TestKafkaHelper

    @Autowired
    lateinit var testDebeziumHelper: TestDebeziumHelper

    @Autowired
    lateinit var testUuidSource: TestUuidSource


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

        beforeTest {
            testUuidSource.disableQueueMode()
        }
    }
}