package com.kien.microservices.gatewayservice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.cloud.config.enabled=false",
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=some-url",
        "spring.cloud.stream.defaultBinder=rabbit"
    ]
)
class GatewayServerApplicationTests {

    @Test
    internal fun `context loads`() {}
}
