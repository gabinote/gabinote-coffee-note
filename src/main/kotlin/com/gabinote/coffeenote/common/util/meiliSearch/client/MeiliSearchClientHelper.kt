package com.gabinote.coffeenote.common.util.meiliSearch.client

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.common.util.meiliSearch.client.data.PagedIndexRes
import com.gabinote.coffeenote.common.util.meiliSearch.client.data.QueryReq
import com.gabinote.coffeenote.common.util.meiliSearch.client.data.QueryRes
import com.gabinote.coffeenote.common.util.meiliSearch.client.data.TaskRes
import com.gabinote.coffeenote.common.util.meiliSearch.client.exception.MeiliSearchConnectionException
import com.gabinote.coffeenote.common.util.meiliSearch.client.exception.MeiliSearchJsonParseException
import com.gabinote.coffeenote.common.util.meiliSearch.client.exception.MeiliSearchRequestException
import com.meilisearch.sdk.Index
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClient

private val logger = KotlinLogging.logger {}

@Component
class MeiliSearchClientHelper(
    private val objectMapper: ObjectMapper,
) {

    @Value($$"${meilisearch.url}")
    lateinit var url: String

    @Value($$"${meilisearch.apiKey}")
    lateinit var apiKey: String
    private lateinit var restClient: RestClient


    @PostConstruct
    fun init() {
        this.restClient = RestClient.builder()
            .baseUrl(url)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    fun Index.query(queryReq: QueryReq): QueryRes {
        return query(queryReq, this.uid)
    }


    fun query(queryReq: QueryReq, indexUid: String): QueryRes {
        val uri = "/indexes/$indexUid/search"

        val requestBody = objectMapper.writeValueAsString(queryReq)

        logger.debug { "Request body: $requestBody" }

        val res = execute {
            restClient
                .post()
                .uri(uri)
                .body(requestBody)
                .retrieve()
                .body(QueryRes::class.java)
        }

        logger.debug { "Res: $res" }

        return res
    }

    fun insert(data: Object, indexUid: String): TaskRes {
        val uri = "/indexes/$indexUid/documents"

        val requestBody = objectMapper.writeValueAsString(data)

        logger.debug { "Request body: $requestBody" }

        val res = execute {
            restClient
                .post()
                .uri(uri)
                .body(requestBody)
                .retrieve()
                .body(TaskRes::class.java)
        }

        logger.debug { "Res: $res" }

        return res
    }

    fun getAllIndexes(): PagedIndexRes {
        val uri = "/indexes"

        val res = execute {
            restClient
                .get()
                .uri(uri)
                .retrieve()
                .body(PagedIndexRes::class.java)
        }

        logger.debug { "Res: $res" }

        return res
    }


    fun deleteIndex(indexUid: String): TaskRes {
        val uri = "/indexes/$indexUid"

        val res = execute {
            restClient
                .delete()
                .uri(uri)
                .retrieve()
                .body(TaskRes::class.java)
        }

        logger.debug { "Res: $res" }

        return res
    }

    fun updateIndexSettings(indexUid: String, settings: Map<String, Any>): TaskRes {
        val uri = "/indexes/$indexUid/settings"

        val requestBody = objectMapper.writeValueAsString(settings)

        logger.debug { "Request body: $requestBody" }

        val res = execute {
            restClient
                .post()
                .uri(uri)
                .body(requestBody)
                .retrieve()
                .body(TaskRes::class.java)
        }

        logger.debug { "Res: $res" }

        return res
    }

    /**
     * 특정 작업이 완료될 때까지 대기합니다.
     * @param taskUid 작업 UID
     * @param timeout 최대 대기 시간 (밀리초 단위, 기본값: 60000ms)
     * @return 작업이 성공적으로 완료되면 true, 실패하면 false
     * @throws MeiliSearchRequestException 작업이 타임아웃되면 발생
     */
    fun waitForTask(taskUid: Int, timeout: Long = 60000): Boolean {
        val uri = "/tasks/$taskUid"

        val startTime = System.currentTimeMillis()

        while (true) {
            val res = execute {
                restClient
                    .get()
                    .uri(uri)
                    .retrieve()
                    .body(TaskRes::class.java)
            }

            logger.debug { "Task status: ${res.status}" }

            if (res.status == "succeeded") {
                return true
            } else if (res.status == "failed") {
                logger.warn { "Task status: ${res.status}" }
                return false
            }

            if (System.currentTimeMillis() - startTime > timeout) {
                throw MeiliSearchRequestException(
                    message = "MeiliSearch task timed out after $timeout ms",
                    statusCode = 504,
                    responseBodyAsString = "Task $taskUid did not complete in time"
                )
            }

            Thread.sleep(100) // 100ms 대기 후 재시도
        }
    }

    private fun <T> execute(apiCall: () -> T?): T {
        try {
            val result = apiCall()

            if (result == null) {
                logger.warn { "MeiliSearch API returned empty or null body" }
                throw MeiliSearchJsonParseException("Received empty or null response body")
            }

            return result

        } catch (e: JsonProcessingException) { // 직렬화 실패
            logger.error(e) { "Failed to serialize MeiliSearch request " }
            throw MeiliSearchJsonParseException("Failed to serialize request body: ${e.message}")

        } catch (e: HttpClientErrorException) { // 4xx
            logger.warn { "MeiliSearch client error: ${e.message} Response: ${e.responseBodyAsString}" }
            throw MeiliSearchRequestException(
                message = "Failed to Request MeiliSearch: ${e.message}",
                statusCode = e.statusCode.value(),
                responseBodyAsString = e.responseBodyAsString,
            )

        } catch (e: HttpServerErrorException) { // 5xx
            logger.warn { "MeiliSearch server error: ${e.message} Response: ${e.responseBodyAsString}" }
            throw MeiliSearchRequestException(
                message = "Failed to Request MeiliSearch: ${e.message}",
                statusCode = e.statusCode.value(),
                responseBodyAsString = e.responseBodyAsString,
            )

        } catch (e: ResourceAccessException) { // 네트워크 에러
            logger.error(e) { "MeiliSearch resource access error: ${e.message}" }
            throw MeiliSearchConnectionException("Failed to connect to MeiliSearch: ${e.message}")


        } catch (e: HttpMessageNotReadableException) { // 역직렬화 실패
            logger.error(e) { "Failed to parse MeiliSearch response " }
            throw MeiliSearchJsonParseException("Failed to parse response body: ${e.message}")

        } catch (e: Exception) { // 기타 모든 예외
            logger.error(e) { "Unexpected error during MeiliSearch API call: ${e.message}" }
            throw e
        }
    }
}