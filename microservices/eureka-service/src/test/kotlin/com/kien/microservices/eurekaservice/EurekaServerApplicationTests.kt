package com.kien.microservices.eurekaservice

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.cloud.config.enabled=false"
    ]
)
class EurekaServerApplicationTests(
    @Autowired private val testRestTemplate: TestRestTemplate,
    @Value("\${app.eureka.username}") private val username: String,
    @Value("\${app.eureka.password}") private val password: String,
) {

    @Test
    internal fun `context loads`() {
    }

    @Test
    internal fun `catalog loads`() {
        val expectedResponseBody = """
            {
                "applications": {
                    "versions__delta": "1",
                    "apps__hashcode": "",
                    "application": []
                }
            }
        """.trimIndent()

        val entity = testRestTemplate.withBasicAuth(username, password)
            .getForEntity("/eureka/apps", String::class.java)

        entity.statusCode shouldBe HttpStatus.OK
        entity.body shouldMatchJson expectedResponseBody
    }

    @Test
    fun healthy() {
        val expectedResponseBody = """
            {
                "status": "UP"
            }
        """
        val entity = testRestTemplate
            .withBasicAuth(username, password)
            .getForEntity("/actuator/health", String::class.java)
        entity.statusCode shouldBe HttpStatus.OK
        entity.body shouldMatchJson expectedResponseBody
    }
}