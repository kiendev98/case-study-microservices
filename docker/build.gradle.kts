plugins {
    id("com.avast.gradle.docker-compose")
}

dockerCompose {
    useComposeFiles.add("docker/docker-compose.yml")

    nested("database").apply {
        startedServices.add("database")
    }
}