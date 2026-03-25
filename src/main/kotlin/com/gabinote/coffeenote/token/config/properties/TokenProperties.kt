package com.gabinote.coffeenote.token.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "keycloak.proxy")
open class TokenProperties(
    var allowedRedirectUri: List<String>,
    var allowedIdpHints: List<String>,
)