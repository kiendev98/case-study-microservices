import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.bmuschko.docker-remote-api")
}

repositories {
    mavenCentral()
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

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


tasks.withType<KotlinCompile> {
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
    images.add("kien/${project.name}:latest")
    buildArgs.put("JAR_FILE", bootJar.archiveFileName)
}