package com.kien.organization

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.context.config.annotation.RefreshScope

@SpringBootApplication
@RefreshScope
@EnableDiscoveryClient
class OrganizationServiceApplication

fun main(args: Array<String>) {
    runApplication<OrganizationServiceApplication>(*args)
}