package com.kien.microservices.eurekaservice

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayServerApplicationTests(
    @Autowired private val testRestTemplate: TestRestTemplate
) {

    @Test
    fun contextLoads() {
    }
}