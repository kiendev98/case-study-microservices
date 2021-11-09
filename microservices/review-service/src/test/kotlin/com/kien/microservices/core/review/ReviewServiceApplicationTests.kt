package com.kien.microservices.core.review

import com.kien.api.core.review.Review
import com.kien.microservices.core.review.persistence.ReviewRepository
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReviewServiceApplicationTests(
    @Autowired private val client: WebTestClient,
    @Autowired private val repository: ReviewRepository
) : PostgreSqlTestBase() {

    @BeforeEach
    fun setupDb() {
        repository.deleteAll()
    }

    @Test
    fun `should return with product id`() {

        val productId = 1

        repository.findByProductId(productId).size shouldBe 0

        postReview(productId, 1)
            .expectStatus().isOk
        postReview(productId, 2)
            .expectStatus().isOk
        postReview(productId, 3)
            .expectStatus().isOk

        repository.findByProductId(productId).size shouldBe 3

        getReview(productId)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(3)
            .jsonPath("$[2].productId").isEqualTo(productId)
            .jsonPath("$[2].reviewId").isEqualTo(3)
    }

    @Test
    fun `should return error when saving duplicated review`() {
        val productId = 1
        val reviewId = 1

        repository.count() shouldBe 0

        postReview(productId, reviewId)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.productId").isEqualTo(productId)
            .jsonPath("$.reviewId").isEqualTo(reviewId)

        repository.count() shouldBe 1

        postReview(productId, reviewId)
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/review")
            .jsonPath("$.message").isEqualTo("Duplicate key, Product Id: 1, Review Id:1")

        repository.count() shouldBe 1
    }

    @Test
    fun `should delete review`() {
        val productId = 1
        val reviewId = 1

        postReview(productId, reviewId)
            .expectStatus().isOk


        repository.findByProductId(productId).size shouldBe 1

        deleteReview(productId)
            .expectStatus().isOk

        repository.findByProductId(productId).size shouldBe 0

        deleteReview(productId)
            .expectStatus().isOk
    }

    @Test
    fun `should return error message when parameters are missing`() {
        getReview("")
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.path").isEqualTo("/review")
            .jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present")
    }

    @Test
    fun `should return error message when parameters are invalid`() {
        getReview("?productId=no-integer")
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.path").isEqualTo("/review")
            .jsonPath("$.message").isEqualTo("Type mismatch.")
    }

    @Test
    fun `should return empty list when not found any reviews`() {
        getReview("?productId=213")
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(0)
    }

    @Test
    fun `should return error message when parameters are negative`() {
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
            .uri("/review${recommendationPath}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)

    private fun postReview(productId: Int, reviewId: Int): WebTestClient.ResponseSpec =
        client.post()
            .uri("/review")
            .body(
                Mono.just(
                    Review(
                        productId,
                        reviewId,
                        "Author $reviewId",
                        "Subject $reviewId",
                        "Content $reviewId",
                        "SA"
                    )
                ),
                Review::class.java
            )
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)

    private fun deleteReview(productId: Int): WebTestClient.ResponseSpec =
        client.delete()
            .uri("/review?productId=$productId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

}