package com.kien.microservices.composite.product.services

import com.kien.api.composite.product.ProductAggregate
import com.kien.api.composite.product.ProductCompositeService
import com.kien.api.composite.product.RecommendationSummary
import com.kien.api.composite.product.ServiceAddresses
import com.kien.api.core.product.Product
import com.kien.api.core.recommendation.Recommendation
import com.kien.api.core.review.Review
import com.kien.util.http.ServiceUtil
import com.kien.util.logs.logWithClass
import org.springframework.boot.logging.LoggerGroup
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
            integration.create
        }
    }

    override fun deleteProduct(productId: Int) {
        TODO("Not yet implemented")
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
            reviews,
            ServiceAddresses(
                serviceAddress,
                product.serviceAddress!!,
                recommendations.recommendationServiceAddress,
                reviews.reviewServiceAddress
            )
        )

}

private val List<Recommendation>.recommendationServiceAddress: String
    get() = first().serviceAddress

private val List<Review>.reviewServiceAddress: String
    get() = first().serviceAddress

private fun List<Recommendation>.summarised(): List<RecommendationSummary> =
    map { RecommendationSummary(it.recommendationId, it.author, it.rate) }