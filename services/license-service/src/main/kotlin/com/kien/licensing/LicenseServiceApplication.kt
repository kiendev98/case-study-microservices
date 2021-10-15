package com.kien.licensing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LicenseServiceApplication

fun main(args: Array<String>) {
    runApplication<LicenseServiceApplication>(*args)
}