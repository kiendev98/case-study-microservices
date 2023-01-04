package com.kien.api.composite.product

data class ReviewSummary(
    val reviewId: Int,
    val author: String,
    val subject: String,
    val content: String
)
