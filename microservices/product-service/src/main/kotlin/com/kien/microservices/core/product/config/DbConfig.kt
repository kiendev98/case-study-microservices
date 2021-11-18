package com.kien.microservices.core.product.config

import com.kien.microservices.core.product.persistence.ProductEntity
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.index.IndexResolver
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations

@Configuration
class DbConfig(
    private val mongoTemplate: ReactiveMongoOperations
) {

    @EventListener(ContextRefreshedEvent::class)
    fun initIndicesAfterStartup() {
        val mappingContext = mongoTemplate.converter.mappingContext
        val resolver: IndexResolver = MongoPersistentEntityIndexResolver(mappingContext)

        val indexOps: ReactiveIndexOperations = mongoTemplate.indexOps(ProductEntity::class.java)
        resolver.resolveIndexFor(ProductEntity::class.java).forEach {
            indexOps.ensureIndex(it).block()
        }
    }
}