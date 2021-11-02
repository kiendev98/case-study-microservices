package com.kien.api.composite.product

import com.kien.api.core.review.Review

data class ProductAggregate(
    val productId: Int,
    val name: String,
    val weight: Int,
    val recommendations: List<RecommendationSummary>,
    val reviews: List<Review>,
    val serviceAddresses: ServiceAddresses
)