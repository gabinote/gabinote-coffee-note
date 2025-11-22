package com.gabinote.coffeenote.common.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.meilisearch.sdk.Client
import com.meilisearch.sdk.Config
import com.meilisearch.sdk.json.JacksonJsonHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MeiliSearchConfig(
    private val objectMapper: ObjectMapper,

    ) {

    @Value($$"${meilisearch.url}")
    lateinit var url: String

    @Value($$"${meilisearch.apiKey}")
    lateinit var apiKey: String

    var mapper: ObjectMapper = objectMapper.copy()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)
        .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)

    @Bean
    fun meiliSearchClient(): Client {

        val jackson = JacksonJsonHandler(mapper)
        val config = Config(url, apiKey, jackson)
//        val config = Config(url, apiKey)
        return Client(config)
    }

}