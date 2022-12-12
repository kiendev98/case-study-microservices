package com.kien.microservices.authorization.config

import com.kien.microservices.authorization.jose.generateRsa
import com.kien.util.logs.logWithClass
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.ClientSettings
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings
import org.springframework.security.oauth2.server.authorization.config.TokenSettings
import java.time.Duration
import java.util.*

val LOG = logWithClass<AuthorizationServerConfig>()

@Configuration(proxyBeanMethods = false)
class AuthorizationServerConfig {

    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        LOG.info("register OAUth client allowing all grant flows...")

        val writerClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("writer")
            .clientSecret("{noop}writerSecret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .redirectUri("https://my.redirect.uri")
            .redirectUri("https://localhost:8443/webjars/swagger-ui/oauth2-redirect.html")
            .scope(OidcScopes.OPENID)
            .scope("product:read")
            .scope("product:write")
            .clientSettings(
                ClientSettings.builder()
                    .requireAuthorizationConsent(true)
                    .build()
            )
            .tokenSettings(
                TokenSettings.builder()
                    .accessTokenTimeToLive(
                        Duration.ofHours(1)
                    )
                    .build()
            )
            .build()

        val readerClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("reader")
            .clientSecret("{noop}readerSecret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .redirectUri("https://my.redirect.uri")
            .redirectUri("https://localhost:8443/webjars/swagger-ui/oauth2-redirect.html")
            .scope(OidcScopes.OPENID)
            .scope("product:read")
            .clientSettings(
                ClientSettings.builder()
                    .requireAuthorizationConsent(true)
                    .build()
            )
            .tokenSettings(
                TokenSettings.builder()
                    .accessTokenTimeToLive(
                        Duration.ofHours(1)
                    )
                    .build()
            )
            .build()

        return InMemoryRegisteredClientRepository(writerClient, readerClient)
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val  rsaKey = generateRsa()
        val jwkSet = JWKSet(rsaKey)
        return JWKSource { jwkSelector, _ ->
            jwkSelector.select(jwkSet)
        }
    }

    @Bean
    fun providerSettings(): ProviderSettings =
        ProviderSettings.builder()
            .issuer("http://authorization:9999")
            .build()
}