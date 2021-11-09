package com.kien.microservices.composite.product.services

import com.kien.api.composite.product.*
import com.kien.api.core.product.Product
import com.kien.api.core.recommendation.Recommendation
import com.kien.api.core.review.Review
import com.kien.util.http.ServiceUtil
import com.kien.util.logs.logWithClass
import org.springframework.web.bind.annotation.RestController

private val LOG = logWithClass<ProductCompositeServiceImpl>()

@RestController
class ProductCompositeServiceImpl(
    private val serviceUtil: ServiceUtil,
    private val integration: ProductCompositeIntegration
) : ProductCompositeService {

    private val serviceAddress: String
        get() = serviceUtil.serviceAddress

    override fun getProduct(productId: Int): ProductAggregate =
        createProductAggregate(
            integration.getProduct(productId),
            integration.getRecommendations(productId),
            integration.getReviews(productId)
        )

    override fun createProduct(body: ProductAggregate) = try {
        LOG.debug("createCompositeProduct: creates a new composite entity for productId: {}", body.productId)
        val product = Product(
            productId = body.productId,
            name = body.name,
            weight = body.weight
        )
        integration.createProduct(product)

        body.recommendations.forEach {
            val recommendation = Recommendation(
                body.productId,
                it.recommendationId,
                it.author,
                it.rate,
                it.content,
                null
            )
            integration.createRecommendation(recommendation)
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
            integration.createReview(review)
        }

        LOG.debug("createCompositeProduct: composite entities created for productId: {}", body.productId)
    } catch (rex: RuntimeException) {
        LOG.warn("createCompositeProduct failed", rex)
        throw rex
    }

    override fun deleteProduct(productId: Int) {
        LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId)

        integration.deleteProduct(productId)

        integration.deleteRecommendations(productId)

        integration.deleteReviews(productId)

        LOG.debug("deleteCompositeProduct: aggregate entities deleted for productId: {}", productId)
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