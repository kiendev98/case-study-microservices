package com.kien.microservices.composite.product.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kien.api.core.product.Product
import com.kien.api.core.product.ProductService
import com.kien.api.core.recommendation.Recommendation
import com.kien.api.core.recommendation.RecommendationService
import com.kien.api.core.review.Review
import com.kien.api.core.review.ReviewService
import com.kien.api.exceptions.InvalidInputException
import com.kien.api.exceptions.NotFoundException
import com.kien.util.http.HttpErrorInfo
import com.kien.util.logs.logWithClass
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.io.IOException

private val LOG = logWithClass<ProductCompositeIntegration>()

@Component
class ProductCompositeIntegration(
    private val restTemplate: RestTemplate,
    private val mapper: ObjectMapper,
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

    init {
        productServiceUrl = "http://$productServiceHost:$productServicePort/product/"
        recommendationServiceUrl =
            "http://$recommendationServiceHost:$recommendationServicePort/recommendation?productId="
        reviewServiceUrl = "http://$reviewServiceHost:$reviewServicePort/review?productId="
    }

    override fun getProduct(productId: Int): Product {
        return try {
            val url = productServiceUrl + productId

            LOG.debug("Will call getProduct API on URL: {}", url)

            val product: Product = restTemplate.getForObject(url, Product::class.java)!!

            LOG.debug("Found a product with id: {}", product.productId)

            product
        } catch (ex: HttpClientErrorException) {
            when (ex.statusCode) {
                HttpStatus.NOT_FOUND -> throw NotFoundException(ex.errorMessage)
                HttpStatus.UNPROCESSABLE_ENTITY -> throw InvalidInputException(ex.errorMessage)
                else -> {
                    LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.statusCode)
                    LOG.warn("Error body: {}", ex.responseBodyAsString)
                    throw ex
                }
            }
        }
    }

    private val HttpClientErrorException.errorMessage: String
        get() {
            return try {
                mapper.readValue(responseBodyAsString, HttpErrorInfo::class.java).message!!
            } catch (ioex: IOException) {
                message!!
            }
        }

    override fun getRecommendations(productId: Int): List<Recommendation> =
        try {
            val url = recommendationServiceUrl + productId
            LOG.debug("Will call getRecommendations API on URL: {}", url)

            val recommendations: List<Recommendation> = restTemplate
                .exchange<List<Recommendation>>(
                    url,
                    HttpMethod.GET,
                    null,
                    object : ParameterizedTypeReference<List<Recommendation>>() {})
                .body!!

            LOG.debug("Found {} recommendations for a product with id: {}", recommendations.size, productId)
            recommendations
        } catch (ex: Exception) {
            LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.message)
            emptyList()
        }

    override fun getReviews(productId: Int): List<Review> =
        try {
            val url = reviewServiceUrl + productId
            LOG.debug("Will call getReviews API on URL: {}", url)

            val reviews: List<Review> = restTemplate
                .exchange<List<Review>>(
                    url,
                    HttpMethod.GET,
                    null,
                    object : ParameterizedTypeReference<List<Review>>() {})
                .body!!

            LOG.debug("Found {} reviews for a product with id: {}", reviews.size, productId)
            reviews
        } catch (ex: Exception) {
            LOG.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.message)
            emptyList()
        }
}
