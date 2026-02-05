package com.gabinote.coffeenote.common.util.meiliSearch.client.exception

class MeiliSearchRequestException(
    override val message: String = "MeiliSearch Request Error",
    val statusCode: Int,
    val responseBodyAsString: String,
) : Exception() {
    override fun toString(): String {
        return "MeiliSearchRequestException(message='$message', statusCode=$statusCode, responseBodyAsString='$responseBodyAsString')"
    }
}