package com.gabinote.coffeenote.common.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "keycloak.admin-client")
data class KeycloakProperties(
    var serverUrl: String,
    var realm: String,
    var clientId: String,
    var clientSecret: String,
)