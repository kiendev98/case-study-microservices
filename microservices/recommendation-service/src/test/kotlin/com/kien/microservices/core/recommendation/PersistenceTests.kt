package com.kien.microservices.core.recommendation

import com.kien.microservices.core.recommendation.persistence.RecommendationEntity
import com.kien.microservices.core.recommendation.persistence.RecommendationRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.OptimisticLockingFailureException

@DataMongoTest(excludeAutoConfiguration = [EmbeddedMongoAutoConfiguration::class])
class PersistenceTests(
    @Autowired private val repository: RecommendationRepository
) : MongoDbTestBase() {

    private lateinit var savedEntity: RecommendationEntity

    @BeforeEach
    fun setupDb() {
        repository.deleteAll()
        val entity = RecommendationEntity(
            1,
            2,
            "a",
            3,
            "c"
        )
        savedEntity = repository.save(entity)

        savedEntity shouldBeEqual entity
    }

    @Test
    fun `should create recommendation`() {
        val newEntity = RecommendationEntity(1, 3, "a", 3, "c")
        repository.save(newEntity)
        val foundEntity = repository.findById(newEntity.id!!).get()
        foundEntity shouldBeEqual newEntity
        repository.count() shouldBe 2
    }

    @Test
    fun `should update recommendation`() {
        savedEntity.author = ("a2")
        repository.save(savedEntity)
        val foundEntity = repository.findById(savedEntity.id!!).get()

        foundEntity.version shouldBe 1
        foundEntity.author shouldBe "a2"
    }

    @Test
    fun `should delete recommendation`() {
        repository.delete(savedEntity)
        repository.existsById(savedEntity.id!!) shouldBe false
    }

    @Test
    fun `should return recommendation with Id`() {
        val entities = repository.findByProductId(savedEntity.productId)
        entities shouldHaveSize 1
        entities.first() shouldBeEqual savedEntity
    }

    @Test
    fun `should return error when saving duplicated review`() {
        shouldThrow<DuplicateKeyException> {
            val entity = RecommendationEntity(1, 2, "a", 3, "c")
            repository.save(entity)
        }
    }

    @Test
    fun `should return error when updating stale review`() {

        val entity1 = repository.findById(savedEntity.id!!).get()
        val entity2 = repository.findById(savedEntity.id!!).get()

        // Update the entity using the first entity object
        entity1.author = "a1"
        repository.save(entity1)

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds an old version number, i.e. an Optimistic Lock Error
        shouldThrow<OptimisticLockingFailureException> {
            entity2.author = "a2"
            repository.save(entity2)
        }

        // Get the updated entity from the database and verify its new sate
        val updatedEntity = repository.findById(savedEntity.id!!).get()
        updatedEntity.version shouldBe 1
        updatedEntity.author shouldBe "a1"
    }

    private infix fun RecommendationEntity.shouldBeEqual(entity: RecommendationEntity) {
        id shouldBe entity.id
        version shouldBe entity.version
        productId shouldBe entity.productId
        recommendationId shouldBe entity.recommendationId
        author shouldBe entity.author
        rating shouldBe entity.rating
        content shouldBe entity.content
    }
}