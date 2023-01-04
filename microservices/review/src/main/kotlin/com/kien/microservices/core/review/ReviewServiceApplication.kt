package com.kien.microservices.core.review

import com.kien.util.logs.logWithClass
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

val logger = logWithClass<ReviewServiceApplication>()

@SpringBootApplication(scanBasePackages = ["com.kien"])
class ReviewServiceApplication

fun main(args: Array<String>) {
    val ctx = runApplication<ReviewServiceApplication>(*args)

    val postgresqlUri = ctx.environment.getProperty("spring.datasource.url")
    logger.info("Connected to PostgreSQL: $postgresqlUri")
}
