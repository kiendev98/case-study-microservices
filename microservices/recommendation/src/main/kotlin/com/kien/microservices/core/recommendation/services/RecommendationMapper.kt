package com.kien.microservices.core.recommendation.services

import com.kien.api.core.recommendation.Recommendation
import com.kien.microservices.core.recommendation.persistence.RecommendationEntity

fun RecommendationEntity.toApi(): Recommendation =
    Recommendation(
        productId = this.productId,
        recommendationId = this.recommendationId,
        author = this.author,
        rate = this.rating,
        content = this.content,
    )

fun Recommendation.toEntity(): RecommendationEntity =
    RecommendationEntity(
        productId = this.productId,
        recommendationId = this.recommendationId,
        author = this.author,
        rating = this.rate,
        content = this.content
    )
