package com.kien.microservices.composite.product.config

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.OAuthFlow
import io.swagger.v3.oas.annotations.security.OAuthFlows
import io.swagger.v3.oas.annotations.security.OAuthScope
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@SecurityScheme(
    name = "security_auth",
    type = SecuritySchemeType.OAUTH2,
    flows = OAuthFlows(
        authorizationCode = OAuthFlow(
            authorizationUrl = "\${springdoc.oAuthFlow.authorizationUrl}",
            tokenUrl = "\${springdoc.oAuthFlow.tokenUrl}",
            scopes = [
                OAuthScope(name = "product:read", description = "read scope"),
                OAuthScope(name = "product:write", description = "write scope")
            ]
        )
    )
)
class ApiConfig(
    @Value("\${api.common.version}") private val apiVersion: String,
    @Value("\${api.common.title}") private val apiTitle: String,
    @Value("\${api.common.description}") private val apiDescription: String,
    @Value("\${api.common.termsOfService}") private val apiTermsOfService: String,
    @Value("\${api.common.license}") private val apiLicense: String,
    @Value("\${api.common.licenseUrl}") private val apiLicenseUrl: String,
    @Value("\${api.common.externalDocDesc}") private val apiExternalDocDesc: String,
    @Value("\${api.common.externalDocUrl}") private val apiExternalDocUrl: String,
    @Value("\${api.common.contact.name}") private val apiContactName: String,
    @Value("\${api.common.contact.url}") private val apiContactUrl: String,
    @Value("\${api.common.contact.email}") private val apiContactEmail: String
) {

    /**
     * Will exposed on $HOST:$PORT/swagger-io.html
     *
     * @return the common OpenAPI documentation
     */
    @Bean
    fun getOpenApiDocument(): OpenAPI =
        OpenAPI()
            .info(
                Info().title(apiTitle)
                    .description(apiDescription)
                    .version(apiVersion)
                    .contact(
                        Contact().name(apiContactName)
                            .url(apiContactUrl)
                            .email(apiContactEmail)
                    )
                    .termsOfService(apiTermsOfService)
                    .license(
                        License().name(apiLicense)
                            .url(apiLicenseUrl)
                    )
            )
            .externalDocs(
                ExternalDocumentation().description(apiExternalDocDesc)
                    .url(apiExternalDocUrl)
            )
}