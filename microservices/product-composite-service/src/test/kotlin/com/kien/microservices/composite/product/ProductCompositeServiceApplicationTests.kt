package com.kien.microservices.composite.product

import com.kien.api.core.product.Product
import com.kien.api.core.recommendation.Recommendation
import com.kien.api.core.review.Review
import com.kien.api.exceptions.InvalidInputException
import com.kien.api.exceptions.NotFoundException
import com.kien.microservices.composite.product.services.ProductCompositeIntegration
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono


private const val PRODUCT_ID_OK = 1
private const val PRODUCT_ID_NOT_FOUND = 2
private const val PRODUCT_ID_INVALID = 3

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TestSecurityConfig::class],
    properties = [
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=",
        "spring.main.allow-bean-definition-overriding=true",
        "spring.cloud.config.enabled=false"
    ]
)
class ProductCompositeServiceApplicationTests(
    @Autowired private val client: WebTestClient
) {

    @MockkBean
    lateinit var compositeIntegration: ProductCompositeIntegration

    @BeforeEach
    fun setUp() {
        every { compositeIntegration.getProduct(PRODUCT_ID_OK, any(), any()) } returns
                Product(
                    PRODUCT_ID_OK,
                    "name",
                    1,
                    "mock-address"
                ).toMono()

        every { compositeIntegration.getRecommendations(PRODUCT_ID_OK) } returns
                listOf(
                    Recommendation(
                        PRODUCT_ID_OK,
                        1,
                        "author",
                        1,
                        "content",
                        "mock address"
                    )
                ).toFlux()

        every { compositeIntegration.getReviews(PRODUCT_ID_OK) } returns
                listOf(
                    Review(
                        PRODUCT_ID_OK,
                        1,
                        "author",
                        "subject",
                        "content",
                        "mock address"
                    )
                ).toFlux()

        every { compositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND, any(), any()) } throws
                NotFoundException("NOT FOUND: $PRODUCT_ID_NOT_FOUND")

        every { compositeIntegration.getProduct(PRODUCT_ID_INVALID, any(), any()) } throws
                InvalidInputException("INVALID: $PRODUCT_ID_INVALID")
    }

    @Test
    internal fun `context loads`() = Unit

    @Test
    internal fun `get product`() {
        client.get()
            .uri("/product-composite/$PRODUCT_ID_OK")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.productId").isEqualTo(PRODUCT_ID_OK)
            .jsonPath("$.recommendations.length()").isEqualTo(1)
            .jsonPath("$.reviews.length()").isEqualTo(1)
    }

    @Test
    internal fun `get product not found`() {
        client.get()
            .uri("/product-composite/$PRODUCT_ID_NOT_FOUND")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/product-composite/$PRODUCT_ID_NOT_FOUND")
            .jsonPath("$.message").isEqualTo("NOT FOUND: $PRODUCT_ID_NOT_FOUND")
    }

    @Test
    fun `get product with invalid input`() {
        client.get()
            .uri("/product-composite/$PRODUCT_ID_INVALID")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/product-composite/$PRODUCT_ID_INVALID")
            .jsonPath("$.message").isEqualTo("INVALID: $PRODUCT_ID_INVALID")
    }
}