package com.kien.microservices.core.recommendation

import com.kien.api.core.recommendation.Recommendation
import com.kien.api.event.Event
import com.kien.api.event.Type
import com.kien.api.exceptions.InvalidInputException
import com.kien.microservices.core.recommendation.persistence.RecommendationRepository
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
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class RecommendationServiceApplicationTests(
    @Autowired private val client: WebTestClient,
    @Autowired private val repository: RecommendationRepository,
    @Autowired @Qualifier("messageProcessor")
    private val messageProcessor: Consumer<Event<Int, Recommendation>>
) : MongoDbTestBase() {

    @BeforeEach
    fun setupDb() {
        repository.deleteAll().block()
    }

    @Test
    fun `should return the recommendations with product id`() {
        val productId = 1

        sendCreateRecommendationEvent(productId, 1)
        sendCreateRecommendationEvent(productId, 2)
        sendCreateRecommendationEvent(productId, 3)

        StepVerifier.create(repository.findByProductId(productId))
            .expectNextCount(3)
            .verifyComplete()

        getRecommendation(productId)
            .expectStatus().isEqualTo(HttpStatus.OK)
            .expectBody()
            .jsonPath("$.length()").isEqualTo(3)
            .jsonPath("$[2].productId").isEqualTo(productId)
            .jsonPath("$[2].recommendationId").isEqualTo(3)
    }

    @Test
    fun `should return error when saving duplicated recommendation`() {

        val productId = 1
        val recommendationId = 1

        sendCreateRecommendationEvent(productId, recommendationId)

        StepVerifier.create(repository.count())
            .expectNext(1)
            .verifyComplete()

        val thrown = shouldThrow<InvalidInputException> {
            sendCreateRecommendationEvent(productId, recommendationId)
        }

        thrown.message shouldBe "Duplicate key, Product Id: 1, Recommendation Id: 1"
        StepVerifier.create(repository.count())
            .expectNext(1)
            .verifyComplete()
    }

    @Test
    fun `should delete recommendation`() {
        val productId = 1
        val recommendationId = 1

        sendCreateRecommendationEvent(productId, recommendationId)

        StepVerifier.create(repository.findByProductId(productId))
            .expectNextCount(1)
            .verifyComplete()

        sendDeleteRecommendationEvent(productId)

        StepVerifier.create(repository.findByProductId(productId))
            .expectNextCount(0)
            .verifyComplete()

        sendDeleteRecommendationEvent(productId)
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
            .jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present")
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
            .uri("/recommendation$recommendationPath")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)

    private fun sendCreateRecommendationEvent(productId: Int, recommendationId: Int) {
        messageProcessor.accept(
            Event(
                Type.CREATE, productId,
                Recommendation(
                    productId,
                    recommendationId,
                    "Author $recommendationId",
                    recommendationId,
                    "Content $recommendationId",
                    "SA"
                )
            )
        )
    }

    private fun sendDeleteRecommendationEvent(productId: Int) {
        messageProcessor.accept(Event(Type.DELETE, productId, null))
    }
}
