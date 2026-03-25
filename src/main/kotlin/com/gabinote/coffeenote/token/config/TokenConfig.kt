package com.gabinote.coffeenote.token.config

import com.gabinote.coffeenote.token.config.properties.TokenProperties
import com.gabinote.coffeenote.token.config.properties.TokenRefreshProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(TokenProperties::class, TokenRefreshProperties::class)
@Configuration
class TokenConfig