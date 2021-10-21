package com.kien.eureka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class EurekaServiceApplication

fun main(args: Array<String>) {
    runApplication<EurekaServiceApplication>(*args)
}