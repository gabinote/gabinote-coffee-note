package com.gabinote.coffeenote.token.dto.token.service

data class TokenRefreshResServiceDto(
    val accessToken: String,
    val accessTokenExpiresIn: Long,

    val refreshToken: String,
    val refreshTokenExpiresIn: Long
)