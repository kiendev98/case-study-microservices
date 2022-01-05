package com.kien.microservices.eurekaservice

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EurekaServerApplicationTests(
    @Autowired private val testRestTemplate: TestRestTemplate
) {

    @Test
    fun contextLoads() {
    }

    @Test
    fun catalogLoads() {
        val expectedResponseBody = """
            {
                "applications": {
                    "versions__delta": "1",
                    "apps__hashcode": "",
                    "application": []
                }
            }
        """.trimIndent()

        val entity = testRestTemplate.getForEntity("/eureka/apps", String::class.java)

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
        val entity = testRestTemplate.getForEntity("/actuator/health", String::class.java)
        entity.statusCode shouldBe HttpStatus.OK
        entity.body shouldMatchJson expectedResponseBody
    }
}