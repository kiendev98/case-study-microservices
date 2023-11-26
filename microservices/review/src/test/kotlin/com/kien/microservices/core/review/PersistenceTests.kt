package com.kien.microservices.core.review

import com.kien.microservices.core.review.persistence.ReviewEntity
import com.kien.microservices.core.review.persistence.ReviewRepository
import com.kien.util.test.PostgreSqlTestBase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@DataJpaTest(
    properties = [
        "spring.jpa.hibernate.ddl-auto=update",
    ]
)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
internal class PersistenceTests(
    @Autowired private val repository: ReviewRepository
) : PostgreSqlTestBase() {

    lateinit var savedEntity: ReviewEntity

    @BeforeEach
    internal fun setupDb() {
        repository.deleteAll()
        val entity = ReviewEntity(1, 2, "a", "s", "c")
        savedEntity = repository.save(entity)

        savedEntity shouldBeEqual entity
    }

    @Test
    internal fun `should create review`() {
        val newEntity = ReviewEntity(1, 3, "a", "s", "c")
        repository.save(newEntity)
        val foundEntity = repository.findById(newEntity.id!!).get()

        foundEntity shouldBeEqual newEntity
        repository.count() shouldBe 2
    }

    @Test
    internal fun `should update review`() {
        savedEntity.author = "a2"
        repository.save(savedEntity)
        val foundEntity = repository.findById(savedEntity.id!!).get()

        foundEntity.version shouldBe 1
        foundEntity.author shouldBe "a2"
    }

    @Test
    internal fun `should delete review`() {
        repository.delete(savedEntity)
        repository.existsById(savedEntity.id!!) shouldBe false
    }

    @Test
    internal fun `should return review with Id`() {
        val entities: List<ReviewEntity> = repository.findByProductId(savedEntity.productId)
        entities.size shouldBe 1
        entities.first() shouldBeEqual savedEntity
    }

    @Test
    fun `should return error when saving duplicated review`() {
        shouldThrow<DataIntegrityViolationException> {
            val entity = ReviewEntity(1, 2, "a", "s", "c")
            repository.save(entity)
        }
    }

    @Test
    fun `should return error when updating stale review`() {

        // Store the saved entity in two separate entity objects
        val entity1 = repository.findById(savedEntity.id!!).get()
        val entity2 = repository.findById(savedEntity.id!!).get()

        // Update the entity using the first entity object
        entity1.author = "a1"
        repository.save(entity1)

        // Update the entity using the second entity object.
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

    private infix fun ReviewEntity.shouldBeEqual(entity: ReviewEntity) {
        id shouldBe entity.id
        version shouldBe entity.version
        productId shouldBe entity.productId
        reviewId shouldBe entity.reviewId
        author shouldBe entity.author
        subject shouldBe entity.subject
        content shouldBe entity.content
    }
}
