package com.kien.microservices.core.recommendation.services

import com.kien.api.core.recommendation.Recommendation
import com.kien.api.core.recommendation.RecommendationService
import com.kien.api.exceptions.InvalidInputException
import com.kien.util.http.ServiceUtil
import com.kien.util.logs.logWithClass
import org.springframework.web.bind.annotation.RestController

private val LOG = logWithClass<RecommendationServiceImpl>()

@RestController
class RecommendationServiceImpl(
    private val serviceUtil: ServiceUtil
) : RecommendationService {
    override fun getRecommendations(productId: Int): List<Recommendation> {

        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        }

        if (productId == 113) {
            LOG.debug("No recommendations found for productId: {}", productId)
            return emptyList()
        }

        val list = listOf<Recommendation>(
            Recommendation(productId, 1, "Author 1", 1, "Content 1", serviceUtil.serviceAddress),
            Recommendation(productId, 2, "Author 2", 2, "Content 2", serviceUtil.serviceAddress),
            Recommendation(productId, 3, "Author 3", 3, "Content 3", serviceUtil.serviceAddress)
        )

        LOG.debug("/recommendation response size: {}", list.size)
        return list
    }
}