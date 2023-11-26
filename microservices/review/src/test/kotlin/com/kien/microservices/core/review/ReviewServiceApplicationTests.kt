package com.kien.microservices.core.review

import com.kien.api.core.review.Review
import com.kien.api.event.Event
import com.kien.api.event.Type
import com.kien.api.exceptions.InvalidInputException
import com.kien.microservices.core.review.persistence.ReviewRepository
import com.kien.util.test.PostgreSqlTestBase
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
import java.util.function.Consumer

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.jpa.hibernate.ddl-auto=update",
    ]
)
internal class ReviewServiceApplicationTests(
    @Autowired private val client: WebTestClient,
    @Autowired private val repository: ReviewRepository,
    @Autowired @Qualifier("messageProcessor")
    private val messageProcessor: Consumer<Event<Int, Review>>
) : PostgreSqlTestBase() {

    @BeforeEach
    internal fun setupDb() {
        repository.deleteAll()
    }

    @Test
    internal fun `should return with product id`() {

        val productId = 1

        repository.findByProductId(productId).size shouldBe 0

        sendCreateReviewEvent(productId, 1)
        sendCreateReviewEvent(productId, 2)
        sendCreateReviewEvent(productId, 3)

        repository.findByProductId(productId).size shouldBe 3

        getReview(productId)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(3)
            .jsonPath("$[2].productId").isEqualTo(productId)
            .jsonPath("$[2].reviewId").isEqualTo(3)
    }

    @Test
    internal fun `should return error when saving duplicated review`() {
        val productId = 1
        val reviewId = 1

        repository.count() shouldBe 0

        sendCreateReviewEvent(productId, reviewId)

        repository.count() shouldBe 1

        val thrown = shouldThrow<InvalidInputException> {
            sendCreateReviewEvent(productId, reviewId)
        }

        thrown.message shouldBe "Duplicate key, Product Id: 1, Review Id: 1"
        repository.count() shouldBe 1
    }

    @Test
    internal fun `should delete review`() {
        val productId = 1
        val reviewId = 1

        sendCreateReviewEvent(productId, reviewId)

        repository.findByProductId(productId).size shouldBe 1

        sendDeleteReviewEvent(productId)

        repository.findByProductId(productId).size shouldBe 0

        sendDeleteReviewEvent(productId)
    }

    @Test
    internal fun `should return error message when parameters are missing`() {
        getReview("")
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.path").isEqualTo("/review")
            .jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present")
    }

    @Test
    internal fun `should return error message when parameters are invalid`() {
        getReview("?productId=no-integer")
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.path").isEqualTo("/review")
            .jsonPath("$.message").isEqualTo("Type mismatch.")
    }

    @Test
    internal fun `should return empty list when not found any reviews`() {
        getReview("?productId=213")
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(0)
    }

    @Test
    internal fun `should return error message when parameters are negative`() {
        val productIdInvalid = -1
        getReview("?productId=$productIdInvalid")
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/review")
            .jsonPath("$.message").isEqualTo("Invalid productId: $productIdInvalid")
    }

    private fun getReview(productId: Int): WebTestClient.ResponseSpec =
        getReview("?productId=$productId")

    private fun getReview(recommendationPath: String): WebTestClient.ResponseSpec =
        client.get()
            .uri("/review$recommendationPath")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)

    private fun sendCreateReviewEvent(productId: Int, reviewId: Int) {
        val review = Review(
            productId, reviewId,
            "Author $reviewId", "Subject $reviewId", "Content $reviewId", "SA"
        )
        val event = Event(Type.CREATE, productId, review)
        messageProcessor.accept(event)
    }

    private fun sendDeleteReviewEvent(productId: Int) {
        messageProcessor.accept(Event(Type.DELETE, productId, null))
    }
}
