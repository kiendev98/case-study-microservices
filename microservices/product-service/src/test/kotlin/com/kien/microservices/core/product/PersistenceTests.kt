package com.kien.microservices.core.product

import com.kien.microservices.core.product.persistence.ProductEntity
import com.kien.microservices.core.product.persistence.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import reactor.test.StepVerifier
import javax.swing.plaf.synth.SynthTextFieldUI

@DataMongoTest(excludeAutoConfiguration = [EmbeddedMongoAutoConfiguration::class])
class PersistenceTests(
    @Autowired private val repository: ProductRepository
) : MongoDbTestBase() {

    private lateinit var savedEntity: ProductEntity

    @BeforeEach
    fun setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();

        val entity = ProductEntity(1, "n", 1)
        StepVerifier.create(repository.save(entity))
            .expectNextMatches {
                savedEntity = it
                entity shouldBeEqual savedEntity
            }
            .verifyComplete()
    }

    @Test
    fun `should create entity`() {
        val newEntity = ProductEntity(2, "n", 2)

        StepVerifier.create(repository.save(newEntity))
            .expectNextMatches { it.productId == newEntity.productId }
            .verifyComplete()

        StepVerifier.create(repository.findById(newEntity.id!!))
            .expectNextMatches { newEntity shouldBeEqual it }

        StepVerifier.create(repository.count())
            .expectNext(2)
            .verifyComplete()
    }

    @Test
    fun `should update entity`() {
        savedEntity.name = "n2"

        StepVerifier.create(repository.save(savedEntity))
            .expectNextMatches { it.name == "n2" }
            .verifyComplete()

        StepVerifier.create(repository.findById(savedEntity.id!!))
            .expectNextMatches { it.version == 1 && it.name == "n2" }
            .verifyComplete()
    }

    @Test
    fun `should delete entity`() {
        StepVerifier.create(repository.delete(savedEntity)).verifyComplete()
        StepVerifier.create(repository.existsById(savedEntity.id!!)).expectNext(false)
    }

    @Test
    fun `should return product`() {
        StepVerifier.create(repository.findByProductId(savedEntity.productId))
            .expectNextMatches { it shouldBeEqual savedEntity }
            .verifyComplete()
    }

    @Test
    fun `should return error when saving duplicated product`() {
        val entity = ProductEntity(savedEntity.productId, "n", 1)
        StepVerifier.create(repository.save(entity))
            .expectError(DuplicateKeyException::class.java)
            .verify()
    }

    @Test
    fun `should return error when updating stale product`() {
        val entity1 = repository.findById(savedEntity.id!!).block()!!
        val entity2 = repository.findById(savedEntity.id!!).block()!!

        entity1.name = "n1"
        repository.save(entity1).block()

        StepVerifier.create(repository.save(entity2))
            .expectError(OptimisticLockingFailureException::class.java)
            .verify()

        StepVerifier.create(repository.findById(savedEntity.id!!))
            .expectNextMatches { it.version == 1 && it.name == "n1" }
            .verifyComplete()
    }

    private infix fun ProductEntity.shouldBeEqual(product: ProductEntity): Boolean =
        id == product.id &&
                version == product.version &&
                productId == product.productId &&
                name == product.name &&
                weight == product.weight
}