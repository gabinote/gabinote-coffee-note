package com.gabinote.coffeenote.common.config

import com.gabinote.coffeenote.common.config.properties.KeycloakProperties
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(KeycloakProperties::class)
@Configuration
class KeycloakConfig(
    private val keycloakProperties: KeycloakProperties,
) {


    /*
   *  Keycloak 서버와 통신하기 위한 클라이언트 빌더
   * */
    @Bean
    fun keycloak(): Keycloak {
        return KeycloakBuilder.builder()
            .serverUrl(keycloakProperties.serverUrl)
            .realm(keycloakProperties.realm)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .clientId(keycloakProperties.clientId)
            .clientSecret(keycloakProperties.clientSecret)
            .build()
    }
}