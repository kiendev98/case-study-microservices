package com.kien.licensing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.context.config.annotation.RefreshScope

@SpringBootApplication
@RefreshScope
class LicenseServiceApplication

fun main(args: Array<String>) {
    runApplication<LicenseServiceApplication>(*args)
}