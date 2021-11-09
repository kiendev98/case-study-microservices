plugins {
    id("com.avast.gradle.docker-compose")
}

dockerCompose {
    useComposeFiles.add("docker-compose.yml")

    nested("database").apply {
        startedServices.addAll("mongodb", "postgresql")
    }
}