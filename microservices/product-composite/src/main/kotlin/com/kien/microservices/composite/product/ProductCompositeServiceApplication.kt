package com.kien.microservices.composite.product

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.kien"])
class ProductCompositeServiceApplication

fun main(args: Array<String>) {
    runApplication<ProductCompositeServiceApplication>(*args)
}
