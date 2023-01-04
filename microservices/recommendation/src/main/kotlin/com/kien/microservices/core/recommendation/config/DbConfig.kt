package com.kien.microservices.core.recommendation.config

import com.kien.microservices.core.recommendation.persistence.RecommendationEntity
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

        val indexOps: ReactiveIndexOperations = mongoTemplate.indexOps(RecommendationEntity::class.java)
        resolver.resolveIndexFor(RecommendationEntity::class.java).forEach {
            indexOps.ensureIndex(it).block()
        }
    }
}
