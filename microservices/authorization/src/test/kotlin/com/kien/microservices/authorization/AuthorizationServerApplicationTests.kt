package com.kien.microservices.authorization

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.cloud.config.enabled=false",
        "spring.cloud.stream.defaultBinder=rabbit"
    ]
)
@AutoConfigureMockMvc
class AuthorizationServerApplicationTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    internal fun `context loads`() {}

    @Test
    internal fun `request token using client credentials grant type`() {
        mockMvc.perform(
            post("/oauth2/token")
                .param("grant_type", "client_credentials")
                .with(basicAuth("reader", "readerSecret"))
        ).andExpect(status().isOk)
    }

    @Test
    internal fun `request openid configuration`() {
        mockMvc.perform(get("/.well-known/openid-configuration"))
            .andExpect(status().isOk)
    }

    @Test
    internal fun `request jwk set`() {
        mockMvc.perform(get("/oauth2/jwks"))
            .andExpect(status().isOk)
    }

    private fun basicAuth(username: String, password: String): RequestPostProcessor {
        return RequestPostProcessor { request ->
            val headers = HttpHeaders().apply {
                setBasicAuth(username, password)
            }
            headers.getFirst("Authorization")?.let {
                request.addHeader("Authorization", it)
            }
            request
        }
    }
}
