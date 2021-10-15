plugins {
    id("com.avast.gradle.docker-compose")
}

dockerCompose {
    useComposeFiles.add("docker/docker-compose.yml")
}