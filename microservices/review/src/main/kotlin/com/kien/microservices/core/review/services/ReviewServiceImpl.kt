package com.kien.microservices.core.review.services

import com.kien.api.core.review.Review
import com.kien.api.core.review.ReviewService
import com.kien.api.exceptions.InvalidInputException
import com.kien.microservices.core.review.persistence.ReviewRepository
import com.kien.util.http.ServiceUtil
import com.kien.util.logs.logWithClass
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.Callable
import java.util.logging.Level.FINE

private val logger = logWithClass<ReviewServiceImpl>()

@RestController
class ReviewServiceImpl(
    private val serviceUtil: ServiceUtil,
    private val repository: ReviewRepository,
    @Qualifier("jdbcScheduler") private val jdbcScheduler: Scheduler
) : ReviewService {

    override fun createReview(body: Review): Mono<Review> =
        if (body.productId < 1) {
            throw InvalidInputException("Invalid productId: ${body.productId}")
        } else {
            { internalCreateReview(body) }.toMono()
                .subscribeOn(jdbcScheduler)
        }

    override fun getReviews(productId: Int): Flux<Review> =
        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        } else {
            logger.info("Will get reviews for product with id={}", productId)

            Callable { internalGetReviews(productId) }.toMono()
                .flatMapMany { it.toFlux() }
                .log(logger.name, FINE)
                .subscribeOn(jdbcScheduler)
        }

    override fun deleteReviews(productId: Int): Mono<Void> =
        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        } else {
            Callable { internalDeleteReviews(productId) }.toMono()
                .subscribeOn(jdbcScheduler)
                .then()
        }

    private fun internalGetReviews(productId: Int): List<Review> =
        repository.findByProductId(productId)
            .map { it.toApi() }
            .onEach { it.serviceAddress = serviceUtil.serviceAddress }
            .apply { logger.debug("getReviews: response size: {}", size) }

    private fun internalCreateReview(body: Review): Review =
        try {
            body.toEntity()
                .let { repository.save(it) }
                .apply {
                    logger.debug(
                        "createReview: created a review entity: {}/{}",
                        body.productId,
                        body.reviewId
                    )
                }
                .toApi()
        } catch (dive: DataIntegrityViolationException) {
            throw InvalidInputException(
                "Duplicate key, Product Id: ${body.productId}, Review Id: ${body.reviewId}"
            )
        }

    private fun internalDeleteReviews(productId: Int) {
        logger.debug(
            "deleteReviews: tries to delete reviews for the product with productId: {}",
            productId
        )
        repository.deleteAll(repository.findByProductId(productId))
    }
}
