import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.bmuschko.docker-remote-api")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.kien.microservices.composite.product"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_16

val springCloudVersion: String by project
val kotestVersion: String by project
val mockkVersion: String by project
val springMockkVersion: String by project
val springDocOpenApiVersion: String by project

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
    }
}


repositories {
    mavenCentral()
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation(project(":api"))
    implementation(project(":util"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springdoc:springdoc-openapi-webflux-ui:$springDocOpenApiVersion")

    testImplementation("com.ninja-squad:springmockk:$springMockkVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(
            group = "org.assertj"
        )
        exclude(
            group = "org.mockito"
        )
    }
    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "16"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<Jar>("jar") {
    enabled = false
}

val bootJar by tasks.getting(BootJar::class)

tasks.register("prepareBuildImage", Copy::class) {
    dependsOn("bootJar")
    from("docker/Dockerfile")
    from("$buildDir/libs") {
        include("*.jar")
    }
    into("$buildDir/docker")
}

tasks.register("buildImages", DockerBuildImage::class) {
    dependsOn("prepareBuildImage")
    inputDir.set(file("$buildDir/docker"))
    images.add("${rootProject.name}/${project.name}:latest")
    images.add("${rootProject.name}/${project.name}:$version")
    buildArgs.put("JAR_FILE", bootJar.archiveFileName)
}