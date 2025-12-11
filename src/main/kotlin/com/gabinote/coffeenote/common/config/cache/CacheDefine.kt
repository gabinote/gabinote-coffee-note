package com.gabinote.coffeenote.common.config.cache

enum class CacheDefine(
    val cacheName: String,
    val expireAfterWriteMinutes: Long = 10L,
    val maximumSize: Long = 1000L,
) {
    CONFIG_CACHE(
        cacheName = "policy",
        expireAfterWriteMinutes = 15L,
        maximumSize = 500L,
    ),
}