package com.gabinote.coffeenote.token.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "keycloak.proxy.refresh-cookie")
data class TokenRefreshProperties(
    var name: String,
    var maxAge: Long,
    var allowPath: String,
    var allowDomain: String,
)