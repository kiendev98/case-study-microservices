package com.kien.api.composite.product

data class RecommendationSummary(
    val recommendationId: Int,
    val author: String,
    val content: String,
    val rate: Int
)