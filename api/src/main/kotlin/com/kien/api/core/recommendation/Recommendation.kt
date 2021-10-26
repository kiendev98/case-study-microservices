package com.kien.api.core.recommendation

data class Recommendation(
    val productId: String,
    val recommendationId: String,
    val author: String,
    val rate: Int,
    val content: String,
    val serviceAddress: String
)