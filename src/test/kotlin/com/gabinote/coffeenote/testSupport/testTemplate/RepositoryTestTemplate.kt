package com.gabinote.coffeenote.testSupport.testTemplate



import com.gabinote.coffeenote.common.config.JacksonConfig
import com.gabinote.coffeenote.testSupport.testConfig.db.UseTestDatabase
import com.gabinote.coffeenote.testSupport.testUtil.data.TestDataHelper
import com.gabinote.coffeenote.testSupport.testUtil.uuid.TestUuidSource

import io.kotest.core.spec.style.DescribeSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest

import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource


@ExtendWith(MockKExtension::class)
@Import(
    JacksonConfig::class,
    TestDataHelper::class,
    TestUuidSource::class,

)
@DataMongoTest
@UseTestDatabase
abstract class RepositoryTestTemplate : DescribeSpec() {

    @Autowired
    lateinit var testDataHelper: TestDataHelper


}