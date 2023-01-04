package com.kien.microservices.core.product

import com.kien.util.logs.logWithClass
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

val logger = logWithClass<ProductServiceApplication>()

@SpringBootApplication(scanBasePackages = ["com.kien"])
class ProductServiceApplication

fun main(args: Array<String>) {
    val applicationContext = runApplication<ProductServiceApplication>(*args)

    val mongoDbHost = applicationContext.environment.getProperty("spring.data.mongodb.host")
    val mongoDbPort = applicationContext.environment.getProperty("spring.data.mongodb.port")

    logger.info("Connected to MongoDb: $mongoDbHost:$mongoDbPort")
}
