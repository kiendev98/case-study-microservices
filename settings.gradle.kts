rootProject.name = "case-study-microservices"

pluginManagement {
    val kotlinVersion: String by settings
    val dockerComposeVersion: String by settings
    val springDependencyManagementVersion: String by settings
    val springBootVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.jpa") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
        id("com.avast.gradle.docker-compose") version dockerComposeVersion
    }
}

include("docker")

include("api")
include("util")
include("microservices:configuration-service")
include("microservices:authorization-service")
include("microservices:gateway-service")
include("microservices:product-service")
include("microservices:review-service")
include("microservices:recommendation-service")
include("microservices:product-composite-service")
