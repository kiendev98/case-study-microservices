import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("io.spring.dependency-management")
    id("java-test-fixtures")
}

group = "com.kien.microservices.util"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val springBootVersion: String by project
val testContainerVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    //Spring
    implementation(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation(project(":api"))

    //Test Fixtures Dependencies
    testFixturesImplementation(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    testFixturesImplementation(platform("org.testcontainers:testcontainers-bom:$testContainerVersion"))
    testFixturesImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
        exclude(module = "mockito-core")
        exclude(module = "assertj-core")
        exclude(module = "android-json")
        exclude(module = "junit-vintage-engine")
    }
    testFixturesImplementation("org.testcontainers:testcontainers")
    testFixturesImplementation("org.testcontainers:postgresql")
    testFixturesImplementation("org.testcontainers:mongodb")
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