package com.kien.microservices.core.recommendation

import com.kien.util.logs.logWithClass
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.kien"])
class RecommendationServiceApplication

val logger = logWithClass<RecommendationServiceApplication>()

fun main(args: Array<String>) {
    val applicationContext = runApplication<RecommendationServiceApplication>(*args)

    val mongoDbHost = applicationContext.environment.getProperty("spring.data.mongodb.host")
    val mongoDbPort = applicationContext.environment.getProperty("spring.data.mongodb.port")

    logger.info("Connected to MongoDb: $mongoDbHost:$mongoDbPort")
}
