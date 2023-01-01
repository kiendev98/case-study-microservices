package com.kien.microservices.composite.product

import com.kien.api.composite.product.ProductAggregate
import com.kien.api.composite.product.RecommendationSummary
import com.kien.api.composite.product.ReviewSummary
import com.kien.api.core.product.Product
import com.kien.api.core.recommendation.Recommendation
import com.kien.api.core.review.Review
import com.kien.api.event.Event
import com.kien.api.event.Type
import com.kien.util.logs.logWithClass
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.OutputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.messaging.Message
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.util.Collections.singletonList

private val LOG = logWithClass<MessagingTests>()

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TestSecurityConfig::class],
    properties = [
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=",
        "spring.main.allow-bean-definition-overriding=true",
        "spring.cloud.config.enabled=false",
        "spring.cloud.stream.defaultBinder=rabbit"
    ]
)
@Import(value = [TestChannelBinderConfiguration::class])
class MessagingTests(
    @Autowired private val target: OutputDestination,
    @Autowired private val client: WebTestClient
)  {

    @BeforeEach
    fun setUp() {
        purgeMessages("products")
        purgeMessages("recommendations")
        purgeMessages("reviews")
    }

    @Test
    internal fun `Verify creating composite product 1`() {
        val composite = ProductAggregate(1, "name", 1)
        postAndVerifyProduct(composite, HttpStatus.ACCEPTED)

        val productMessages = getMessages("products")
        val recommendationMessages = getMessages("recommendations")
        val reviewMessages = getMessages("reviews")

        productMessages.size shouldBe 1

        val expectedEvent = Event(
            Type.CREATE,
            composite.productId,
            Product(composite.productId, composite.name, composite.weight)
        )


        expectedEvent.withoutCreatedAt() shouldEqualJson productMessages.first().withoutCreatedAt()

        recommendationMessages.size shouldBe 0
        reviewMessages.size shouldBe 0
    }

    @Test
    fun `Verify creating composite product 2`() {
        val composite = ProductAggregate(
            1,
            "name",
            1,
            singletonList(RecommendationSummary(1, "a", "c", 1)),
            singletonList(ReviewSummary(1, "a", "s", "c"))
        )
        postAndVerifyProduct(composite, HttpStatus.ACCEPTED)

        val productMessages = getMessages("products")
        val recommendationMessages = getMessages("recommendations")
        val reviewMessages = getMessages("reviews")

        productMessages.size shouldBe 1
        val expectedProductEvent = Event(
            Type.CREATE,
            composite.productId,
            Product(composite.productId, composite.name, composite.weight)
        )
        expectedProductEvent.withoutCreatedAt() shouldEqualJson productMessages.first().withoutCreatedAt()

        recommendationMessages.size shouldBe 1
        val rec = composite.recommendations.first()
        val expectRecommendationEvent = Event(
            Type.CREATE,
            composite.productId,
            Recommendation(
                composite.productId,
                rec.recommendationId,
                rec.author,
                rec.rate,
                rec.content
            )
        )
        expectRecommendationEvent.withoutCreatedAt() shouldEqualJson recommendationMessages.first().withoutCreatedAt()


        reviewMessages.size shouldBe 1
        val rev = composite.reviews.first()
        val expectReviewEvent = Event(
            Type.CREATE,
            composite.productId,
            Review(
                composite.productId,
                rev.reviewId,
                rev.author,
                rev.subject,
                rev.content
            )
        )
        expectReviewEvent.withoutCreatedAt() shouldEqualJson reviewMessages.first().withoutCreatedAt()
    }

    @Test
    fun `Verify deleting composite product`() {
        deleteAndVerifyProduct(1, HttpStatus.ACCEPTED)
        val productMessages = getMessages("products")
        val recommendationMessages = getMessages("recommendations")
        val reviewMessages = getMessages("reviews")

        productMessages.size shouldBe 1
        val expectedProductEvent = Event(Type.DELETE, 1, null)
        expectedProductEvent.withoutCreatedAt() shouldEqualJson productMessages.first().withoutCreatedAt()

        recommendationMessages.size shouldBe 1
        val expectedRecommendationEvent = Event(Type.DELETE, 1, null)
        expectedRecommendationEvent.withoutCreatedAt() shouldEqualJson recommendationMessages.first().withoutCreatedAt()

        reviewMessages.size shouldBe 1
        val expectedReviewEvent = Event(Type.DELETE, 1, null)
        expectedReviewEvent.withoutCreatedAt() shouldEqualJson reviewMessages.first().withoutCreatedAt()
    }

    private fun postAndVerifyProduct(compositeProduct: ProductAggregate, expectedStatus: HttpStatus) {
        client.post()
            .uri("/product-composite")
            .body(Mono.just<Any>(compositeProduct), ProductAggregate::class.java)
            .exchange()
            .expectStatus().isEqualTo(expectedStatus)
    }

    private fun deleteAndVerifyProduct(productId: Int, expectedStatus: HttpStatus) {
        client.delete()
            .uri("/product-composite/$productId")
            .exchange()
            .expectStatus().isEqualTo(expectedStatus)
    }

    private fun purgeMessages(bindingName: String) {
        getMessages(bindingName)
    }

    private fun getMessages(bindingName: String): List<EventJsonString> {
        val messages = mutableListOf<String>()

        var anyMoreMessages = true

        while (anyMoreMessages) {
            val message = getMessage(bindingName)

            if (message == null) {
                anyMoreMessages = false
            } else {
                messages.add(String(message.payload))
            }
        }

        return messages
    }

    private fun getMessage(bindingName: String): Message<ByteArray>? =
        try {
            target.receive(0, bindingName)
        } catch (ex: NullPointerException) {
            LOG.error("getMessage() received a NPE with binding = {}", bindingName)
            null
        }
}