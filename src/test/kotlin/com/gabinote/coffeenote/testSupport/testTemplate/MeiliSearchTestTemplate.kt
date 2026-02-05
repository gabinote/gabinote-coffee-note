package com.gabinote.coffeenote.testSupport.testTemplate

import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.common.config.JacksonConfig
import com.gabinote.coffeenote.testSupport.testConfig.meiliSearch.UseTestMeiliSearch
import com.gabinote.coffeenote.testSupport.testUtil.meilisearch.TestMeiliSearchHelper
import com.meilisearch.sdk.Client
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension


@UseTestMeiliSearch
//@UseJackson
@Import(
    TestMeiliSearchHelper::class,
    JacksonConfig::class,
    ObjectMapper::class

)
@ExtendWith(MockKExtension::class, SpringExtension::class)
abstract class MeiliSearchTestTemplate : DescribeSpec() {

    @Autowired
    lateinit var testMeiliSearchHelper: TestMeiliSearchHelper

    @Autowired
    lateinit var meiliSearchClient: Client

    open val baseIndexDir = "/testsets/note/domain/noteIndex/index"
    open val baseDataDir = "/testsets/note/domain/noteIndex/data"
    open val baseData = "base.json"
    open val baseIndex = "base-index.json"
    fun useBaseData() {
        useBaseIndex()
        testMeiliSearchHelper.insertData("$baseDataDir/$baseData")
    }

    fun useBaseIndex() {
        testMeiliSearchHelper.insertIndex("$baseIndexDir/$baseIndex")
    }

}