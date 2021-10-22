import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.bmuschko.docker-remote-api")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.kien"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val springCloudVersion: String by project

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

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client") {
        exclude(
            group = "com.netflix.ribbon",
            module = "ribbon-eureka"
        )
    }
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")

    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("io.r2dbc:r2dbc-postgresql")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
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