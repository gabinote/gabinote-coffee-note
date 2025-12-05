package com.gabinote.coffeenote.common.util.hash

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.google.common.hash.Hashing.murmur3_128
import org.springframework.stereotype.Component


@Component
class HashHelper {
    val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
    }

    private fun generateCanonicalJson(data: Any): String {

        try {
            val mapData = objectMapper.convertValue(data, Map::class.java)

            return objectMapper.writeValueAsString(mapData)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to generate canonical JSON", e)
        }
    }

    private fun hashString(input: String): String {
        try {
            val hash = murmur3_128().hashUnencodedChars(input).toString()
            return hash
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to hash string", e)
        }
    }

    fun generateHash(data: Any): String {
        val canonicalJson = generateCanonicalJson(data)
        return hashString(canonicalJson)
    }

    fun compareHash(data: Any, expectedHash: String): Boolean {
        val generatedHash = generateHash(data)
        return generatedHash == expectedHash
    }

    fun compareHash(targetHash: String, expectedHash: String): Boolean {
        return targetHash == expectedHash
    }

}