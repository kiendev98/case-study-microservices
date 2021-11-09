package com.kien.api.composite.product

data class ProductAggregate(
    val productId: Int,
    val name: String,
    val weight: Int,
    val recommendations: List<RecommendationSummary>,
    val reviews: List<ReviewSummary>,
    var serviceAddresses: ServiceAddresses? = null
)