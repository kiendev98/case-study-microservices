package com.kien.microservices.configuration

import com.kien.util.logs.logWithClass
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

private val LOG = logWithClass<ConfigurationServerApplication>()

@SpringBootApplication(scanBasePackages = ["com.kien"])
@EnableConfigServer
class ConfigurationServerApplication

fun main(args: Array<String>) {
    val context = runApplication<ConfigurationServerApplication>(*args)
    val repoLocation = context.environment.getProperty("spring.cloud.config.server.native.searchLocations")
    LOG.info("Serving configurations from folder: $repoLocation")
}