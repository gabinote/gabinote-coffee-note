package com.gabinote.coffeenote.testSupport.testUtil.debezium

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestComponent
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.nio.charset.StandardCharsets
import java.time.Duration


private val logger = KotlinLogging.logger {}

@TestComponent
class TestDebeziumHelper(
    @Value("\${debezium.connect.url}")
    private val debeziumUrl: String,
    private val objectMapper: ObjectMapper,
) {

    private val restTemplate = RestTemplate()
    private val connectorsEndpoint = "$debeziumUrl/connectors"

    fun registerConnector(configFileName: String) {
        val endpoint = "$debeziumUrl/connectors"

        try {
            val resource = ClassPathResource(configFileName)
            val jsonConfig = String(resource.inputStream.readAllBytes(), StandardCharsets.UTF_8)

            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }

            val request = HttpEntity(jsonConfig, headers)
            restTemplate.postForEntity(endpoint, request, String::class.java)
            waitForConnectorToBeRunning("${objectMapper.readTree(jsonConfig).get("name").asText()}")
            logger.info { "Successfully registered Debezium connector: $configFileName" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to register Debezium connector" }
            throw e
        }
    }

    fun deleteAllConnectors() {
        try {
            val connectorNames = restTemplate.getForObject(connectorsEndpoint, Array<String>::class.java)
            connectorNames?.forEach { name ->
                deleteConnector(name)
            }
            logger.info { "All Debezium connectors have been deleted: ${connectorNames?.joinToString()}" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to delete all Debezium connectors" }
            throw e
        }
    }

    fun deleteConnector(name: String) {
        try {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }
            val entity = HttpEntity<String>(null, headers)

            restTemplate.exchange(
                "$connectorsEndpoint/$name",
                HttpMethod.DELETE,
                entity,
                String::class.java
            )
            waitForConnectorToBeRemoved(name)
            logger.info { "Successfully deleted connector: $name" }

        } catch (e: HttpClientErrorException.NotFound) {
            logger.warn { "Connector $name not found (already deleted)" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to delete connector: $name" }
            throw e
        }
    }

    fun waitForConnectorToBeRemoved(connectorName: String, timeout: Duration = Duration.ofSeconds(10)) {
        val statusEndpoint = "$connectorsEndpoint/$connectorName/status"
        val endTime = System.currentTimeMillis() + timeout.toMillis()

        logger.info { "Waiting for connector '$connectorName' to be removed..." }

        while (System.currentTimeMillis() < endTime) {
            try {
                // 상태 조회 시도
                restTemplate.getForObject(statusEndpoint, String::class.java)
            } catch (e: HttpClientErrorException.NotFound) {
                // 404 Not Found 가 반환되면 삭제된 것임
                logger.info { "Connector '$connectorName' has been removed." }
                return
            } catch (e: Exception) {
                logger.debug { "Connector status check failed (retrying): ${e.message}" }
            }

            Thread.sleep(500) // 0.5초 대기
        }

        logger.error { "Connector '$connectorName' was not removed within ${timeout.seconds} seconds." }
        throw RuntimeException("Connector '$connectorName' was not removed within ${timeout.seconds} seconds")
    }

    fun waitForConnectorToBeRunning(connectorName: String, timeout: Duration = Duration.ofSeconds(10)) {
        val statusEndpoint = "$connectorsEndpoint/$connectorName/status"
        val endTime = System.currentTimeMillis() + timeout.toMillis()

        logger.info { "Waiting for connector '$connectorName' to be RUNNING..." }

        while (System.currentTimeMillis() < endTime) {
            try {
                // 상태 조회
                val response = restTemplate.getForObject(statusEndpoint, String::class.java)

                if (response != null) {
                    val rootNode = objectMapper.readTree(response)

                    // 1. Connector 상태 확인
                    val connectorState = rootNode.path("connector").path("state").asText()

                    // 2. Task 상태 확인 (배열 내 모든 태스크가 RUNNING 이어야 함)
                    val tasksNode = rootNode.path("tasks")
                    val isTasksRunning = if (tasksNode.isArray && tasksNode.size() > 0) {
                        tasksNode.all { it.path("state").asText() == "RUNNING" }
                    } else {
                        false
                    }

                    if (connectorState == "RUNNING" && isTasksRunning) {
                        logger.info { "Connector '$connectorName' is fully RUNNING." }
                        return
                    }
                }
            } catch (e: Exception) {
                // 404 Not Found(아직 등록 안됨)나 연결 오류 시에는 무시하고 재시도
                logger.debug { "Connector status check failed (retrying): ${e.message}" }
            }

            Thread.sleep(500) // 0.5초 대기
        }

        logger.error { "Connector '$connectorName' did not start within ${timeout.seconds} seconds." }
        throw RuntimeException("Connector '$connectorName' failed to reach RUNNING state within ${timeout.seconds} seconds")
    }
}