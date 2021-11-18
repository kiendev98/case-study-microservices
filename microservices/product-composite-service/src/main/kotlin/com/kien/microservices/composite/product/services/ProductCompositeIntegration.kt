package com.kien.microservices.composite.product.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kien.api.core.product.Product
import com.kien.api.core.product.ProductService
import com.kien.api.core.recommendation.Recommendation
import com.kien.api.core.recommendation.RecommendationService
import com.kien.api.core.review.Review
import com.kien.api.core.review.ReviewService
import com.kien.api.event.Event
import com.kien.api.event.Type
import com.kien.api.exceptions.InvalidInputException
import com.kien.api.exceptions.NotFoundException
import com.kien.util.http.HttpErrorInfo
import com.kien.util.logs.logWithClass
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.Health
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.kotlin.core.publisher.toMono
import java.io.IOException
import java.util.concurrent.Callable
import java.util.logging.Level

private val LOG = logWithClass<ProductCompositeIntegration>()

@Component
class ProductCompositeIntegration(
    webClient: WebClient.Builder,
    private val mapper: ObjectMapper,
    private val streamBridge: StreamBridge,
    @Qualifier("publishEventScheduler")
    private val publishEventScheduler: Scheduler,
    @Value("\${app.product-service.host}") productServiceHost: String,
    @Value("\${app.product-service.port}") productServicePort: Int,
    @Value("\${app.recommendation-service.host}") recommendationServiceHost: String,
    @Value("\${app.recommendation-service.port}") recommendationServicePort: Int,
    @Value("\${app.review-service.host}") reviewServiceHost: String,
    @Value("\${app.review-service.port}") reviewServicePort: Int
) : ProductService, RecommendationService, ReviewService {

    private val productServiceUrl: String
    private val recommendationServiceUrl: String
    private val reviewServiceUrl: String
    private val webClient = webClient.build()

    init {
        productServiceUrl = "http://$productServiceHost:$productServicePort/product/"
        recommendationServiceUrl =
            "http://$recommendationServiceHost:$recommendationServicePort/recommendation?productId="
        reviewServiceUrl = "http://$reviewServiceHost:$reviewServicePort/review?productId="
    }

    val reviewHealth: Mono<Health>
        get() = getHealth(reviewServiceUrl)

    val recommendationHealth: Mono<Health>
        get() = getHealth(recommendationServiceUrl)

    val productHealth: Mono<Health>
        get() = getHealth(productServiceUrl)

    override fun createRecommendation(body: Recommendation): Mono<Recommendation> =
        Callable {
            sendMessage(
                "recommendations-out-0",
                Event(Type.CREATE, body.productId, body)
            )
            body
        }.toMono()
            .subscribeOn(publishEventScheduler)

    override fun deleteRecommendations(productId: Int): Mono<Void> =
        Callable {
            sendMessage(
                "recommendations-out-0",
                Event(Type.DELETE, productId)
            )
        }.toMono()
            .subscribeOn(publishEventScheduler).then()

    override fun createReview(body: Review): Mono<Review> =
        Callable {
            sendMessage(
                "reviews-out-0",
                Event(Type.CREATE, body.productId, body)
            )
            body
        }.toMono()
            .subscribeOn(publishEventScheduler)


    override fun deleteReviews(productId: Int): Mono<Void> =
        Callable {
            sendMessage(
                "reviews-out-0",
                Event(Type.DELETE, productId)
            )
        }.toMono()
            .subscribeOn(publishEventScheduler).then()

    override fun getProduct(productId: Int): Mono<Product> =
        "$productServiceUrl/product/$productId"
            .apply { LOG.debug("Will call the getProduct API on URL: {}", this) }
            .let {
                webClient.get()
                    .uri(it)
                    .retrieve()
                    .bodyToMono(Product::class.java)
                    .log(LOG.name, Level.FINE)
                    .onErrorMap(WebClientResponseException::class.java) { handleException(it) }
            }

    override fun createProduct(body: Product): Mono<Product> =
        {
            sendMessage("products-out-0", Event(Type.CREATE, body.productId, body))
            body
        }.toMono()
            .subscribeOn(publishEventScheduler)

    private fun sendMessage(bindingName: String, event: Event<Any, Any>) {
        LOG.debug("Sending a {} message to {}", event.eventType, bindingName)
        val message = MessageBuilder.withPayload(event)
            .setHeader("partitionKey", event.key)
            .build()
        streamBridge.send(bindingName, message)
    }

    private fun handleException(ex: Throwable): Throwable {
        if (ex !is WebClientResponseException) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString())
            return ex
        }

        return when (ex.statusCode) {
            HttpStatus.NOT_FOUND -> NotFoundException(ex.errorMessage)
            HttpStatus.UNPROCESSABLE_ENTITY -> InvalidInputException(ex.errorMessage)
            else -> {
                LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.statusCode)
                LOG.warn("Error body: {}", ex.responseBodyAsString)
                ex
            }
        }
    }

    override fun deleteProduct(productId: Int): Mono<Void> =
        Callable {
            sendMessage(
                "products-out-0",
                Event(Type.DELETE, productId)
            )
        }.toMono()
            .subscribeOn(publishEventScheduler).then()

    private val WebClientResponseException.errorMessage: String
        get() = try {
            mapper.readValue(this.responseBodyAsString, HttpErrorInfo::class.java).message!!
        } catch (ioex: IOException) {
            this.message!!
        }

    override fun getRecommendations(productId: Int): Flux<Recommendation> =
        "$recommendationServiceUrl/recommendation?productId=$productId"
            .apply { LOG.debug("Will call the getRecommendations API on URL: {}", this) }
            .let {
                webClient.get()
                    .uri(it)
                    .retrieve()
                    .bodyToFlux(Recommendation::class.java)
                    .log(LOG.name, Level.FINE)
                    .onErrorResume { Flux.empty() }
            }

    override fun getReviews(productId: Int): Flux<Review> =
        "$reviewServiceUrl/review?productId=$productId"
            .apply { LOG.debug("Will call the getReviews API on URL: {}", this) }
            .let {
                webClient.get()
                    .uri(it)
                    .retrieve()
                    .bodyToFlux(Review::class.java)
                    .log(LOG.name, Level.FINE)
                    .onErrorResume { Flux.empty() }
            }

    private fun getHealth(url: String) =
        "$url/actuator/health"
            .apply { LOG.debug("Will call the Health API on URL: {}", this) }
            .let { actuatorUrl ->
                webClient.get()
                    .uri(actuatorUrl)
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .map { Health.Builder().up().build() }
                    .onErrorResume { Health.Builder().down(it).build().toMono() }
                    .log(LOG.name, Level.FINE)
            }
}
