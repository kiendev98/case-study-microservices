package com.kien.api.core.recommendation

data class Recommendation(
    val productId: Int,
    val recommendationId: Int,
    val author: String,
    val rate: Int,
    val content: String,
    var serviceAddress: String? = null
)
