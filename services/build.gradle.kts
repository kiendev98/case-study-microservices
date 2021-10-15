import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
    id("com.bmuschko.docker-remote-api") version "7.1.0"
    id("com.avast.gradle.docker-compose") version "0.14.9"
    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "com.bmuschko.docker-remote-api")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    extra["springCloudVersion"] = "2020.0.4"

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        }
    }


    dependencies {
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("io.projectreactor:reactor-test")
    }

    java.sourceCompatibility = JavaVersion.VERSION_11

    group = "com.kien"

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
        from("$rootDir/docker/Dockerfile")
        from("$buildDir/libs") {
            include("*.jar")
        }
        into("$buildDir/docker")
    }

    tasks.register("buildImage", DockerBuildImage::class) {
        dependsOn("prepareBuildImage")
        inputDir.set(file("$buildDir/docker"))
        images.add("kien/${project.name}:latest")
        buildArgs.put("JAR_FILE", bootJar.archiveFileName)
    }
}

dockerCompose {
    useComposeFiles.add("docker/docker-compose.yml")
}
