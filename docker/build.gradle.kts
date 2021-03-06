plugins {
    id("com.avast.gradle.docker-compose")
}

dockerCompose {
    useComposeFiles.add("docker-compose.yml")

    nested("database").apply {
        startedServices.addAll("mongodb", "postgresql")
    }

    nested("partitioned").apply {
        useComposeFiles.add("docker-compose-partitions.yml")
    }

    nested("kafka").apply {
        useComposeFiles.add("docker-compose-kafka.yml")
    }
}