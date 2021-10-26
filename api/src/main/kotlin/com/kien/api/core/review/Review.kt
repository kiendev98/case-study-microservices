package com.kien.api.core.review

data class Review(
    val productId: String,
    val reviewId: String,
    val author: String,
    val subject: String,
    val content: String,
    val serviceAddress: String
)