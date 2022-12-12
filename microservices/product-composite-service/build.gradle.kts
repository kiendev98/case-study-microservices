import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.kien.microservices.composite.product"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val springCloudVersion: String by project
val kotestVersion: String by project
val mockkVersion: String by project
val springMockkVersion: String by project
val springDocOpenApiVersion: String by project
val resilience4jVersion: String by project

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
    }

    // Avoid Spring Cloud overriding the version of resilience4j
    dependencies {
        dependency("io.github.resilience4j:resilience4j-spring:$resilience4jVersion")
        dependency("io.github.resilience4j:resilience4j-annotations:$resilience4jVersion")
        dependency("io.github.resilience4j:resilience4j-consumer:$resilience4jVersion")
        dependency("io.github.resilience4j:resilience4j-core:$resilience4jVersion")
        dependency("io.github.resilience4j:resilience4j-circuitbreaker:$resilience4jVersion")
        dependency("io.github.resilience4j:resilience4j-ratelimiter:$resilience4jVersion")
        dependency("io.github.resilience4j:resilience4j-retry:$resilience4jVersion")
        dependency("io.github.resilience4j:resilience4j-bulkhead:$resilience4jVersion")
        dependency("io.github.resilience4j:resilience4j-timelimiter:$resilience4jVersion")
        dependency("io.github.resilience4j:resilience4j-micrometer:$resilience4jVersion")
        dependency("io.github.resilience4j:resilience4j-circularbuffer:$resilience4jVersion")
    }
}


repositories {
    mavenCentral()
}


dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // subprojects
    implementation(project(":api"))
    implementation(project(":util"))

    // Spring boot
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // Springdoc
    implementation("org.springdoc:springdoc-openapi-webflux-ui:$springDocOpenApiVersion")

    // Retry
    implementation("org.springframework.retry:spring-retry")

    // Spring cloud
    implementation("org.springframework.cloud:spring-cloud-starter-stream-rabbit")
    implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
    implementation("org.springframework.cloud:spring-cloud-sleuth-zipkin")

    // Spring security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-oauth2-jose")

    // Resilience4j
    implementation("io.github.resilience4j:resilience4j-spring-boot2:$resilience4jVersion")
    implementation("io.github.resilience4j:resilience4j-reactor:$resilience4jVersion")

    // Test
    testImplementation("com.ninja-squad:springmockk:$springMockkVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
        exclude(module = "mockito-core")
        exclude(module = "assertj-core")
        exclude(module = "android-json")
        exclude(module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.cloud:spring-cloud-stream::test-binder")
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        setEvents(listOf("passed", "failed", "skipped"))
    }
}

tasks.getByName<Jar>("jar") {
    enabled = false
}

tasks.withType<BootBuildImage> {
    builder = "paketobuildpacks/builder:base"
    runImage = "paketobuildpacks/run:base-cnb"
    imageName = "${rootProject.name}/${project.name}"
}

tasks.register("buildImages") {
    dependsOn("bootBuildImage")
}