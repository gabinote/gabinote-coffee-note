package com.gabinote.coffeenote.testSupport.testUtil.meilisearch

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.gabinote.coffeenote.common.config.MeiliSearchConfig
import com.meilisearch.sdk.Client
import com.meilisearch.sdk.SearchRequest
import com.meilisearch.sdk.model.Settings
import com.meilisearch.sdk.model.TaskInfo
import com.meilisearch.sdk.model.TaskStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.TestComponent
import org.springframework.core.io.ClassPathResource


private val logger = KotlinLogging.logger {}

@TestComponent
class TestMeiliSearchHelper(
    private val meiliSearchClient: Client,
    private val meiliSearchConfig: MeiliSearchConfig,
) {
    var objectMapper: ObjectMapper = meiliSearchConfig.mapper


    fun insertIndex(vararg fileNames: String, cleanBefore: Boolean = true) {
        if (cleanBefore) {
            cleanDatabase()
        }

        fileNames.forEach { fileName ->
            logger.info { "Inserting file $fileName" }
            insertIndexPerItem(fileName)
        }
    }

    fun insertData(vararg fileNames: String, cleanBefore: Boolean = false) {
        if (cleanBefore) {
            cleanDatabase()
        }
        fileNames.forEach { fileName ->
            logger.debug { "Inserting file: $fileName" }
            insertPerFile(fileName)
        }
    }


    fun assertData(vararg fileNames: String) {
        fileNames.forEach { fileName ->
            logger.debug { "validate file: $fileName" }
            validatePerFile(fileName)
        }
    }

    private fun cleanDatabase() {
        logger.info { "Cleaning database..." }
        val indexes = meiliSearchClient.indexes
        logger.info { "Indexes: $indexes" }
        indexes.results.forEach { indexInfo ->
            deleteIndex(indexInfo.uid)
        }
    }

    private fun validatePerFile(fileName: String) {
        val validationSet = parseToDataTestSet(fileName)
        logger.info { "Validation set: $validationSet" }

        val actualData = getAllDocuments(validationSet.uid)
        compareData(expected = validationSet.data, actual = actualData)
    }


    private fun compareData(expected: List<Map<String, Any>>, actual: List<Map<String, Any>>, idField: String = "id") {

        actual.size shouldBe expected.size

        val expectedMap = toIdMap(idField, expected)
        val actualMap = toIdMap(idField, actual)

        actualMap.keys shouldBe expectedMap.keys

        for ((key, value) in expectedMap) {
            val actualValue = actualMap[key] ?: throw AssertionError("Key '$key' not found in actual data")
            actualValue shouldBe value
        }


    }

    private fun toIdMap(idField: String, target: List<Map<String, Any>>): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        for (item in target) {
            val idValue = item[idField]?.toString()
                ?: throw IllegalArgumentException("Item does not contain id field '$idField': $item")
            map[idValue] = item
        }
        return map
    }

    private fun insertIndexPerItem(fileName: String) {
        val indexItem = parseToIndexTestSet(fileName)
        insertIndex(indexItem)
    }

    private fun insertPerFile(fileName: String) {
        val dataItem = parseToDataTestSet(fileName)
        insertDocuments(dataItem)
    }

    private fun parseToDataTestSet(fileName: String): DataTestSet {

        val data = parseTestSet<DataTestSet>(fileName)
        return data
    }

    private fun parseToValidationSet(fileName: String): ValidationSet {
        val data = parseTestSet<ValidationSet>(fileName)
        return data
    }

    private fun parseToIndexTestSet(fileName: String): IndexTestSet {
        val data = parseTestSet<IndexTestSet>(fileName)
        return data
    }

    private inline fun <reified T> parseTestSet(fileName: String): T {
        val input = ClassPathResource(fileName)

        val jsonString = input.inputStream.bufferedReader().use { it.readText() }
        return objectMapper.readValue<T>(jsonString)
    }


    private fun insertDocuments(dataTestSet: DataTestSet) {
        val indexInstance = meiliSearchClient.getIndex(dataTestSet.uid)
        val data = objectMapper.writeValueAsString(dataTestSet.data)
        val taskInfo = indexInstance.addDocuments(data)
        meiliSearchClient.waitForTask(taskInfo.taskUid)
        checkStatusFail(taskInfo)
    }

    private fun toSetting(testIndexConfig: IndexConfig): Settings {
        return Settings().apply {
            this.filterableAttributes = testIndexConfig.filterableAttributes.toTypedArray()
            this.faceting = testIndexConfig.faceting.toConfigFormat()
            this.searchableAttributes = testIndexConfig.searchableAttributes.toTypedArray()
        }
    }

    private fun deleteIndex(uid: String) {
        try {
            logger.info { "Deleting index $uid" }
            val taskInfo = meiliSearchClient.deleteIndex(uid)
            meiliSearchClient.waitForTask(taskInfo.taskUid)
            logger.debug { "Deleted index $uid successfully." }
        } catch (e: Exception) {
            logger.warn { "Failed to delete index $uid: ${e.message}" }
        }
    }

    private fun insertIndex(testSet: IndexTestSet) {
        createIndex(testSet)
        val settings = toSetting(testSet.config)
        updateSettings(testSet.uid, settings)
    }

    private fun createIndex(testSet: IndexTestSet) {
        try {
            logger.info { "Creating index ${testSet.uid} with primary key ${testSet.primaryKey}" }
            val taskInfo = meiliSearchClient.createIndex(
                testSet.uid,
                testSet.primaryKey,
            )
            meiliSearchClient.waitForTask(taskInfo.taskUid)
            logger.debug { "Created index ${testSet.uid}successfully." }
            checkStatusFail(taskInfo)
        } catch (e: Exception) {
            logger.error { "Failed to create index ${testSet.uid} : ${e.message}" }
            throw e
        }
    }

    private fun updateSettings(uid: String, settings: Settings) {
        try {
            logger.info { "Updating settings for index $uid: $settings" }
            val index = meiliSearchClient.getIndex(uid)
            val taskInfo = index.updateSettings(settings)
            meiliSearchClient.waitForTask(taskInfo.taskUid)
            logger.debug { "Updated settings for index $uid successfully." }
            checkStatusFail(taskInfo)
        } catch (e: Exception) {
            logger.error { "Failed to update settings for index $uid: ${e.message}" }
            throw e
        }
    }

    private fun checkStatusFail(taskInfo: TaskInfo) {
        if (taskInfo.status == TaskStatus.FAILED) {
            throw IllegalStateException("MeiliSearch task ${taskInfo.taskUid} failed")
        }
    }

    private fun getAllDocuments(uid: String): List<Map<String, Any>> {
        val index = meiliSearchClient.getIndex(uid)
        val searchRequest = SearchRequest("*").apply {
            limit = 1000
        }
        val searchRes = index.search(searchRequest)
        logger.debug { "Search result hits: ${searchRes.hits}" }
        return searchRes.hits
    }
}