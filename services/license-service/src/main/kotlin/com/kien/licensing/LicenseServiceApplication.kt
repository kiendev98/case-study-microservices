package com.kien.licensing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@RefreshScope
@EnableDiscoveryClient
@EnableEurekaClient
class LicenseServiceApplication

fun main(args: Array<String>) {
    runApplication<LicenseServiceApplication>(*args)
}