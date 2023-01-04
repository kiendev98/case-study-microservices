package com.kien.util.test

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer

abstract class MongoDbTestBase {
    companion object {
        private val database = MongoDBContainer("mongo:4.4.2")

        init {
            database.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun setProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.host") { database.host }
            registry.add("spring.data.mongodb.port") { database.getMappedPort(27017) }
            registry.add("spring.data.mongodb.database") { "test" }
        }
    }
}
