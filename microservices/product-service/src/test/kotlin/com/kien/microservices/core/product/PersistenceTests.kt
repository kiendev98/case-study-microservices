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

@DataMongoTest(excludeAutoConfiguration = [EmbeddedMongoAutoConfiguration::class])
class PersistenceTests(
    @Autowired private val repository: ProductRepository
) : MongoDbTestBase() {

    private lateinit var savedEntity: ProductEntity

    @BeforeEach
    fun setupDb() {
        repository.deleteAll()
        val entity = ProductEntity(1, "n", 1)
        savedEntity = repository.save(entity)

        entity shouldBeEqual savedEntity
    }

    @Test
    fun `should create entity`() {
        val newEntity = ProductEntity(2, "n", 2)
        repository.save(newEntity)

        val foundEntity = repository.findById(newEntity.id!!).get()
        newEntity shouldBeEqual foundEntity

        repository.count() shouldBe 2
    }

    @Test
    fun `should update entity`() {
        savedEntity.name = "n2"
    }

    @Test
    fun `should delete entity`() {
        repository.delete(savedEntity)
        repository.existsById(savedEntity.id!!) shouldBe false
    }

    @Test
    fun `should return error when saving duplicated product`() {
        shouldThrow<DuplicateKeyException> {
            val entity = ProductEntity(savedEntity.productId, "n", 1)
            repository.save(entity)
        }
    }

    @Test
    fun `should return error when updating stale product`() {
        val entity1 = repository.findById(savedEntity.id!!).get()
        val entity2 = repository.findById(savedEntity.id!!).get()

        entity1.name = "n1"
        repository.save(entity1)

        shouldThrow<OptimisticLockingFailureException> {
            entity2.name = "n2"
            repository.save(entity2)
        }

        val updatedEntity = repository.findById(savedEntity.id!!).get()
        updatedEntity.version shouldBe 1
        updatedEntity.name shouldBe "n1"
    }

    @Test
    fun `should return paged data`() {
        repository.deleteAll()

        val newProducts = (1001..1010)
            .map { index -> ProductEntity(index, "name $index", index) }

        repository.saveAll(newProducts)

        PageRequest.of(0, 4, Sort.Direction.ASC, "productId")
            .testNextPage(listOf(1001, 1002, 1003, 1004))
            .testNextPage(listOf(1005, 1006, 1007, 1008))
            .testNextPage(listOf(1009, 1010), false)
    }

    private fun Pageable.testNextPage(expectedProductIds: List<Int>, expectsNextPage: Boolean = true): Pageable = let { page ->
        repository.findAll(this)
            .apply {
                content.map { it.productId } shouldContainExactly expectedProductIds
            }
            .apply {
                hasNext() shouldBe expectsNextPage
            }
            page.next()
    }

    private infix fun ProductEntity.shouldBeEqual(product: ProductEntity) {
        id shouldBe product.id
        version shouldBe product.version
        productId shouldBe product.productId
        name shouldBe product.name
        weight shouldBe product.weight
    }
}