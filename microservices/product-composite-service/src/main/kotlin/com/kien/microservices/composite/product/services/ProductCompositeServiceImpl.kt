package com.kien.microservices.composite.product.services

import com.kien.api.composite.product.*
import com.kien.api.core.product.Product
import com.kien.api.core.recommendation.Recommendation
import com.kien.api.core.review.Review
import com.kien.util.http.ServiceUtil
import com.kien.util.logs.logWithClass
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.function.Consumer
import java.util.function.Function
import java.util.logging.Level

private val LOG = logWithClass<ProductCompositeServiceImpl>()

@RestController
class ProductCompositeServiceImpl(
    private val serviceUtil: ServiceUtil,
    private val integration: ProductCompositeIntegration
) : ProductCompositeService {

    private val serviceAddress: String
        get() = serviceUtil.serviceAddress

    @Suppress("UNCHECKED_CAST")
    override fun getProduct(productId: Int): Mono<ProductAggregate> {
        LOG.info("Will get composite product info for product.id={}", productId)
        return Mono.zip(
            {
                createProductAggregate(
                    it[0] as Product,
                    it[1] as List<Recommendation>,
                    it[2] as List<Review>
                )
            },
            integration.getProduct(productId),
            integration.getRecommendations(productId).collectList(),
            integration.getReviews(productId).collectList()
        )
            .doOnError { LOG.warn("getCompositeProduct failed: {}", it.toString()) }
            .log(LOG.name, Level.FINE)
    }

    override fun createProduct(body: ProductAggregate): Mono<Void> = try {
        LOG.debug("createCompositeProduct: creates a new composite entity for productId: {}", body.productId)

        val monos = mutableListOf<Mono<*>>()

        val product = Product(
            productId = body.productId,
            name = body.name,
            weight = body.weight
        )

        monos.add(integration.createProduct(product))

        body.recommendations.forEach {
            val recommendation = Recommendation(
                body.productId,
                it.recommendationId,
                it.author,
                it.rate,
                it.content,
                null
            )
            monos.add(integration.createRecommendation(recommendation))
        }

        body.reviews.forEach {
            val review = Review(
                body.productId,
                it.reviewId,
                it.author,
                it.subject,
                it.content,
                null
            )
            monos.add(integration.createReview(review))
        }

        LOG.debug("createCompositeProduct: composite entities created for productId: {}", body.productId)

        Mono.zip({}, *monos.toTypedArray())
            .doOnError {
                LOG.warn("createCompositeProduct failed: {}", it.toString())
            }.then()

    } catch (rex: RuntimeException) {
        LOG.warn("createCompositeProduct failed", rex)
        throw rex
    }

    override fun deleteProduct(productId: Int): Mono<Void> = try {
        LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId)

        Mono.zip(
            {},
            integration.deleteProduct(productId),
            integration.deleteRecommendations(productId),
            integration.deleteReviews(productId)
        )
            .doOnError { LOG.warn("delete failed: {}", it.toString()) }
            .log(LOG.name, Level.FINE)
            .then()
    } catch (re: RuntimeException) {
        LOG.warn("deleteCompositeProduct failed: {}", re.toString())
        throw re
    }

    private fun createProductAggregate(
        product: Product,
        recommendations: List<Recommendation>,
        reviews: List<Review>
    ): ProductAggregate =
        ProductAggregate(
            product.productId,
            product.name,
            product.weight,
            recommendations.summarised(),
            reviews.summarised(),
            ServiceAddresses(
                serviceAddress,
                product.serviceAddress!!,
                recommendations.serviceAddress,
                reviews.serviceAddress
            )
        )

}

private val List<Recommendation>.serviceAddress: String
    @JvmName("getRecommendationServiceAddress")
    get() = first().serviceAddress!!

private val List<Review>.serviceAddress: String
    @JvmName("getReviewServiceAddress")
    get() = first().serviceAddress!!

@JvmName("summarisedRecommendation")
private fun List<Recommendation>.summarised(): List<RecommendationSummary> =
    map { RecommendationSummary(it.recommendationId, it.author, it.content, it.rate) }

@JvmName("summarisedReview")
private fun List<Review>.summarised(): List<ReviewSummary> =
    map { ReviewSummary(it.reviewId, it.author, it.subject, it.content) }