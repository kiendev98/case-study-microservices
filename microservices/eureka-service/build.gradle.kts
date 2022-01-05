import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.bmuschko.docker-remote-api")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.kien.microservices.eurekaservice"
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

    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
    implementation("org.glassfish.jaxb:jaxb-runtime")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(
            group = "org.assertj"
        )
        exclude(
            group = "org.mockito"
        )
    }
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