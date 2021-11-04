package com.kien.microservices.core.product

import com.kien.api.core.product.Product
import com.kien.microservices.core.product.persistence.ProductRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono.just

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests(
    @Autowired private val client: WebTestClient,
    @Autowired private val repository: ProductRepository
) : MongoDbTestBase() {

    @BeforeEach
    fun setupDb() {
        repository.deleteAll()
    }

    @Test
    fun `should return product with Id`() {
        val productId = 1
        postProduct(1)
            .expectStatus().isEqualTo(HttpStatus.OK)

        repository.findByProductId(productId) shouldNotBe null

        getProduct(1)
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectBody()
            .jsonPath("$.productId").isEqualTo(productId)
    }

    @Test
    fun `should return when post duplicated product`() {
        val productId = 1

        postProduct(1)
            .expectStatus().isEqualTo(HttpStatus.OK)

        repository.findByProductId(productId) shouldNotBe null

        postProduct(productId)
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/product")
            .jsonPath("$.message").isEqualTo("Duplicate key, Product Id: $productId")
    }

    @Test
    fun `should delete product`() {
        val productId = 1
        postProduct(productId)
            .expectStatus().isEqualTo(HttpStatus.OK)

        repository.findByProductId(productId) shouldNotBe null

        deleteProduct(productId)
            .expectStatus().isEqualTo(HttpStatus.OK)

        repository.findByProductId(productId) shouldBe null

        deleteProduct(productId)
            .expectStatus().isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `should return error when parameters are invalid string`() {
        getProduct("/no-integer")
            .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/product/no-integer")
            .jsonPath("$.message").isEqualTo("Type mismatch.")
    }

    @Test
    fun `should return error when product not found`() {
        val productIdNotFound = 13
        getProduct(productIdNotFound)
            .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/product/$productIdNotFound")
            .jsonPath("$.message").isEqualTo("No product found for productId: $productIdNotFound");
    }

    @Test
    fun `should return error when parameters are negative value`() {
        val productIdInvalid = -1
        getProduct(productIdInvalid)
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/product/$productIdInvalid")
            .jsonPath("$.message").isEqualTo("Invalid productId: $productIdInvalid");

    }

    private fun getProduct(productId: Int): WebTestClient.ResponseSpec =
        getProduct("/$productId")

    private fun getProduct(productPath: String): WebTestClient.ResponseSpec =
        client.get()
            .uri("/product${productPath}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)

    private fun postProduct(productId: Int): WebTestClient.ResponseSpec =
        client.post()
            .uri("/product")
            .body(
                just(Product(productId, "Name $productId", productId, "SA")),
                Product::class.java
            )
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)

    private fun deleteProduct(productId: Int): WebTestClient.ResponseSpec =
        client.delete()
            .uri("/product/$productId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
}