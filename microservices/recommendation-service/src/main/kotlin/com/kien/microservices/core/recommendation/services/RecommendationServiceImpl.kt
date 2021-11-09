package com.kien.microservices.core.recommendation.services

import com.kien.api.core.recommendation.Recommendation
import com.kien.api.core.recommendation.RecommendationService
import com.kien.api.exceptions.InvalidInputException
import com.kien.microservices.core.recommendation.persistence.RecommendationRepository
import com.kien.util.http.ServiceUtil
import com.kien.util.logs.logWithClass
import org.springframework.dao.DuplicateKeyException
import org.springframework.web.bind.annotation.RestController

private val LOG = logWithClass<RecommendationServiceImpl>()

@RestController
class RecommendationServiceImpl(
    private val serviceUtil: ServiceUtil,
    private val repository: RecommendationRepository
) : RecommendationService {
    override fun getRecommendations(productId: Int): List<Recommendation> {
        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        }
        return repository.findByProductId(productId)
            .map { it.toApi() }
            .onEach { it.serviceAddress = serviceUtil.serviceAddress }
            .apply { LOG.debug("getRecommendations: response size: {}", size) }
    }

    override fun createRecommendation(body: Recommendation): Recommendation =
        try {
            body.toEntity()
                .let { repository.save(it) }
                .apply {
                    LOG.debug(
                        "createRecommendation: created a recommendation entity: {}/{}",
                        body.productId,
                        body.recommendationId
                    )
                }
                .let { it.toApi() }
        } catch (dke: DuplicateKeyException) {
            throw InvalidInputException(
                "Duplicate key, Product Id: ${body.productId}, Recommendation Id:${body.recommendationId}"
            )
        }


    override fun deleteRecommendations(productId: Int) {
        LOG.debug(
            "deleteRecommendations: tries to delete recommendations for the product with productId: {}",
            productId
        )
        repository.deleteAll(repository.findByProductId(productId))
    }
}