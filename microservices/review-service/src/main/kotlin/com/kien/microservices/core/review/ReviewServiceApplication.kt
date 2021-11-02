package com.kien.microservices.core.review

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(scanBasePackages = ["com.kien"])
class ReviewServiceApplication

fun main(args: Array<String>) {
    runApplication<ReviewServiceApplication>(*args)
}