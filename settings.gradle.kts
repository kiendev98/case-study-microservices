rootProject.name = "case-study-microservices"

pluginManagement {
    val kotlinVersion: String by settings
    val dockerComposeVersion: String by settings
    val springDependencyManagementVersion: String by settings
    val springBootVersion: String by settings
    val dockerVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        id("com.bmuschko.docker-remote-api") version dockerVersion
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
        id("com.avast.gradle.docker-compose") version dockerComposeVersion
    }
}

include("docker")

include("services:license-service")
include("services:organization-service")
include("services:config-service")
include("services:eureka-service")
