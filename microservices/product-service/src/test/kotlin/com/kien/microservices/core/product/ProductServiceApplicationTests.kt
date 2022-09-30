package com.kien.microservices.core.product

import com.kien.api.core.product.Product
import com.kien.api.event.Event
import com.kien.api.event.Type
import com.kien.api.exceptions.InvalidInputException
import com.kien.microservices.core.product.persistence.ProductRepository
import com.kien.util.test.MongoDbTestBase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier
import java.util.function.Consumer

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false"
    ]
)
class ProductServiceApplicationTests(
    @Autowired private val client: WebTestClient,
    @Autowired private val repository: ProductRepository,
    @Autowired @Qualifier("messageProcessor")
    private val messageProcessor: Consumer<Event<Int, Product>>
) : MongoDbTestBase() {

    @BeforeEach
    fun setupDb() {
        repository.deleteAll().block()
    }

    @Test
    fun `should return product with Id`() {
        val productId = 1

        StepVerifier.create(repository.count())
            .expectNext(0)
            .verifyComplete()

        sendCreateProductEvent(productId)

        StepVerifier.create(repository.findByProductId(productId))
            .expectNextCount(1)
            .verifyComplete()

        getProduct(1)
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectBody()
            .jsonPath("$.productId").isEqualTo(productId)
    }

    @Test
    fun `should return when post duplicated product`() {
        val productId = 1

        sendCreateProductEvent(productId)
        StepVerifier.create(repository.findByProductId(productId))
            .expectNextCount(1)
            .verifyComplete()

        val thrown = shouldThrow<InvalidInputException> {
            sendCreateProductEvent(productId)
        }

        thrown.message shouldBe "Duplicate key, Product Id: $productId"
    }

    @Test
    fun `should delete product`() {
        val productId = 1

        sendCreateProductEvent(productId)

        StepVerifier.create(repository.findByProductId(productId))
            .expectNextCount(1)
            .verifyComplete()

        sendDeleteProductEvent(productId)

        StepVerifier.create(repository.findByProductId(productId))
            .expectNextCount(0)
            .verifyComplete()
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

    private fun sendCreateProductEvent(productId: Int) {
        val product = Product(productId, "Name $productId", productId, "SA")
        val event = Event(Type.CREATE, productId, product)
        messageProcessor.accept(event)
    }

    private fun sendDeleteProductEvent(productId: Int) {
        messageProcessor.accept(Event(Type.DELETE, productId, null))
    }
}