package com.kien.microservices.core.recommendation

import com.kien.microservices.core.recommendation.persistence.RecommendationEntity
import com.kien.microservices.core.recommendation.persistence.RecommendationRepository
import com.kien.util.test.MongoDbTestBase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.OptimisticLockingFailureException
import reactor.test.StepVerifier

@DataMongoTest(
    excludeAutoConfiguration = [EmbeddedMongoAutoConfiguration::class],
    properties = [
        "spring.cloud.config.enabled=false",
        "spring.data.mongodb.auto-index-creation=true"
    ]
)
class PersistenceTests(
    @Autowired private val repository: RecommendationRepository
) : MongoDbTestBase() {

    private lateinit var savedEntity: RecommendationEntity

    @BeforeEach
    fun setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete()
        val entity = RecommendationEntity(
            1,
            2,
            "a",
            3,
            "c"
        )

        StepVerifier.create(repository.save(entity))
            .expectNextMatches {
                savedEntity = it
                savedEntity shouldBeEqual entity
            }
            .verifyComplete()
    }

    @Test
    fun `should create recommendation`() {
        val newEntity = RecommendationEntity(1, 3, "a", 3, "c")
        repository.save(newEntity).block()

        StepVerifier.create(repository.findById(newEntity.id!!))
            .expectNextMatches { it shouldBeEqual newEntity }
            .verifyComplete()

        StepVerifier.create(repository.count())
            .expectNext(2)
    }

    @Test
    fun `should update recommendation`() {
        savedEntity.author = ("a2")
        repository.save(savedEntity).block()

        StepVerifier.create(repository.findById(savedEntity.id!!))
            .expectNextMatches { it.version == 1 && it.author == "a2" }
            .verifyComplete()
    }

    @Test
    fun `should delete recommendation`() {
        repository.delete(savedEntity).block()

        StepVerifier.create(repository.existsById(savedEntity.id!!))
            .expectNext(false)
            .verifyComplete()
    }

    @Test
    fun `should return recommendations with productId`() {
        StepVerifier.create(repository.findByProductId(savedEntity.productId))
            .expectNextMatches { it shouldBeEqual savedEntity }
            .verifyComplete()
    }

    @Test
    fun `should return error when saving duplicated review`() {
        val entity = RecommendationEntity(1, 2, "a", 3, "c")

        StepVerifier.create(repository.save(entity))
            .expectError(DuplicateKeyException::class.java)
            .verify()
    }

    @Test
    fun `should return error when updating stale review`() {

        val entity1 = repository.findById(savedEntity.id!!).block()!!
        val entity2 = repository.findById(savedEntity.id!!).block()!!

        // Update the entity using the first entity object
        entity1.author = "a1"
        repository.save(entity1).block()

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds an old version number, i.e. an Optimistic Lock Error
        entity2.author = "a2"
        StepVerifier.create(repository.save(entity2))
            .expectError(OptimisticLockingFailureException::class.java)
            .verify()

        StepVerifier.create(repository.findById(savedEntity.id!!))
            .expectNextMatches { it.version == 1 && it.author == "a1" }
            .verifyComplete()
    }

    private infix fun RecommendationEntity.shouldBeEqual(entity: RecommendationEntity): Boolean =
        id == entity.id &&
        version == entity.version &&
        productId == entity.productId &&
        recommendationId == entity.recommendationId &&
        author == entity.author &&
        rating == entity.rating &&
        content == entity.content
}