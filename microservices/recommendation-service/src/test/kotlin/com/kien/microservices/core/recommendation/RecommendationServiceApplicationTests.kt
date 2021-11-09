package com.kien.microservices.core.recommendation

import com.kien.api.core.product.Product
import com.kien.api.core.recommendation.Recommendation
import com.kien.api.core.review.Review
import com.kien.microservices.core.recommendation.persistence.RecommendationRepository
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
class RecommendationServiceApplicationTests(
    @Autowired private val client: WebTestClient,
    @Autowired private val repository: RecommendationRepository
) : MongoDbTestBase() {

    @BeforeEach
    fun setupDb() {
        repository.deleteAll()
    }

    @Test
    fun `should return the recommendation with id`() {
        val productId = 1

        postRecommendation(productId, 1)
            .expectStatus().isEqualTo(HttpStatus.OK)
        postRecommendation(productId, 2)
            .expectStatus().isEqualTo(HttpStatus.OK)
        postRecommendation(productId, 3)
            .expectStatus().isEqualTo(HttpStatus.OK)

        repository.findByProductId(productId).size shouldBe 3

        getRecommendation(productId)
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectBody()
            .jsonPath("$.length()").isEqualTo(3)
            .jsonPath("$[2].productId").isEqualTo(productId)
            .jsonPath("$[2].recommendationId").isEqualTo(3);
    }

    @Test
    fun `should return error when saving duplicated recommendation`() {

        val productId = 1
        val recommendationId = 1

        postRecommendation(productId, recommendationId)
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectBody()
            .jsonPath("$.productId").isEqualTo(productId)
            .jsonPath("$.recommendationId").isEqualTo(recommendationId)

        repository.count() shouldBe 1

        postRecommendation(productId, recommendationId)
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/recommendation")
            .jsonPath("$.message").isEqualTo("Duplicate key, Product Id: 1, Recommendation Id:1")

        repository.count() shouldBe 1
    }

    @Test
    fun `should delete recommendation`() {
        val productId = 1
        val recommendationId = 1

        postRecommendation(productId, recommendationId)
            .expectStatus().isEqualTo(HttpStatus.OK)

        repository.findByProductId(productId).size shouldBe 1

        deleteRecommendation(productId)
            .expectStatus()
            .isEqualTo(HttpStatus.OK)

        repository.findByProductId(productId).size shouldBe 0

        deleteRecommendation(productId)
            .expectStatus().isOk
    }

    @Test
    fun `should return error when parameters are invalid`() {
        getRecommendation("?productId=no-integer")
            .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/recommendation")
            .jsonPath("$.message").isEqualTo("Type mismatch.")
    }

    @Test
    fun `should return error when parameters are missing`() {
        getRecommendation("")
            .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/recommendation")
            .jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present");
    }


    @Test
    fun `should return error when not found`() {
        getRecommendation("?productId=113")
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectBody()
            .jsonPath("$.length()").isEqualTo(0)
    }

    @Test
    fun `should return error when parameters are negative value`() {
        val productIdInvalid = -1

        getRecommendation("?productId=$productIdInvalid")
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/recommendation")
            .jsonPath("$.message").isEqualTo("Invalid productId: $productIdInvalid")
    }

    private fun getRecommendation(productId: Int): WebTestClient.ResponseSpec =
        getRecommendation("?productId=$productId")

    private fun getRecommendation(recommendationPath: String): WebTestClient.ResponseSpec =
        client.get()
            .uri("/recommendation${recommendationPath}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)

    private fun postRecommendation(productId: Int, recommendationId: Int): WebTestClient.ResponseSpec =
        client.post()
            .uri("/recommendation")
            .body(
                Mono.just(
                    Recommendation(
                        productId,
                        recommendationId,
                        "Author $recommendationId",
                        1,
                        "Content $recommendationId",
                        "SA"
                    )
                ),
                Recommendation::class.java
            )
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)

    private fun deleteRecommendation(productId: Int): WebTestClient.ResponseSpec =
        client.delete()
            .uri("/recommendation?productId=$productId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
}