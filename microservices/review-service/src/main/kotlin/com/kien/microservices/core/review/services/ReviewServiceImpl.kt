package com.kien.microservices.core.review.services

import com.kien.api.core.review.Review
import com.kien.api.core.review.ReviewService
import com.kien.api.exceptions.InvalidInputException
import com.kien.microservices.core.review.persistence.ReviewRepository
import com.kien.util.http.ServiceUtil
import com.kien.util.logs.logWithClass
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.bind.annotation.RestController

private val LOG = logWithClass<ReviewServiceImpl>()

@RestController
class ReviewServiceImpl(
    private val serviceUtil: ServiceUtil,
    private val repository: ReviewRepository
) : ReviewService {
    override fun getReviews(productId: Int): List<Review> {
        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        }

        return repository.findByProductId(productId)
            .map { it.toApi() }
            .onEach { it.serviceAddress = serviceUtil.serviceAddress }
            .apply { LOG.debug("getReviews: response size: {}", size) }
    }

    override fun createReview(body: Review): Review =
        try {
            body.toEntity()
                .let { repository.save(it) }
                .apply {
                    LOG.debug(
                        "createReview: created a review entity: {}/{}",
                        body.productId,
                        body.reviewId
                    )
                }
                .toApi()
        } catch (dive: DataIntegrityViolationException) {
            throw InvalidInputException(
                "Duplicate key, Product Id: ${body.productId}, Review Id:${body.reviewId}"
            )
        }

    override fun deleteReviews(productId: Int) {
        LOG.debug(
            "deleteReviews: tries to delete reviews for the product with productId: {}",
            productId
        )
        repository.deleteAll(repository.findByProductId(productId))
    }
}