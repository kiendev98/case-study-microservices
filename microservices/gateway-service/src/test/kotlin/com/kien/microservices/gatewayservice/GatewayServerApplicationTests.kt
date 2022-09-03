package com.kien.microservices.eurekaservice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = ["eureka.client.enabled=false"])
class GatewayServerApplicationTests {

    @Test
    fun contextLoads() {
    }
}