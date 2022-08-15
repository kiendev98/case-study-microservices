package com.kien.microservices.core.recommendation.services

import com.kien.api.core.recommendation.Recommendation
import com.kien.api.core.recommendation.RecommendationService
import com.kien.api.exceptions.InvalidInputException
import com.kien.microservices.core.recommendation.persistence.RecommendationRepository
import com.kien.util.http.ServiceUtil
import com.kien.util.logs.logWithClass
import org.springframework.dao.DuplicateKeyException
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.logging.Level

private val LOG = logWithClass<RecommendationServiceImpl>()

@RestController
class RecommendationServiceImpl(
    private val serviceUtil: ServiceUtil,
    private val repository: RecommendationRepository
) : RecommendationService {
    override fun getRecommendations(productId: Int): Flux<Recommendation> =
        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        } else {
            LOG.info("Will get recommendations for product with id={}", productId)

            repository.findByProductId(productId)
                .log(LOG.name, Level.FINE)
                .map { it.toApi() }
                .map {
                    it.serviceAddress = serviceUtil.serviceAddress
                    it
                }
        }

    override fun createRecommendation(body: Recommendation): Mono<Recommendation> =
        if (body.productId < 1) {
            throw InvalidInputException("Invalid productId: " + body.productId)
        } else {
            body.toEntity()
                .let { repository.save(it) }
                .log(LOG.name, Level.FINE)
                .onErrorMap(DuplicateKeyException::class.java) {
                    InvalidInputException("Duplicate key, Product Id: ${body.productId}, Recommendation Id: ${body.recommendationId}")
                }
                .map { it.toApi() }
        }


    override fun deleteRecommendations(productId: Int): Mono<Void> =
        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        } else {
            LOG.debug(
                "deleteRecommendations: tries to delete recommendations for the product with productId: {}",
                productId
            )
            repository.deleteAll(repository.findByProductId(productId))
        }
}