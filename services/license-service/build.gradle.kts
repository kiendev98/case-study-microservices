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


repositories {
    mavenCentral()
}


dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
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
    images.add("kien/${project.name}:latest")
    buildArgs.put("JAR_FILE", bootJar.archiveFileName)
}