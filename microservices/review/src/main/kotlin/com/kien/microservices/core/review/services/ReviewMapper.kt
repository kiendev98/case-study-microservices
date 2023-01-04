package com.kien.microservices.core.review.services

import com.kien.api.core.review.Review
import com.kien.microservices.core.review.persistence.ReviewEntity

fun ReviewEntity.toApi(): Review =
    Review(
        productId = this.productId,
        reviewId = this.reviewId,
        author = this.author,
        subject = this.subject,
        content = this.content
    )

fun Review.toEntity(): ReviewEntity =
    ReviewEntity(
        reviewId = this.reviewId,
        productId = this.productId,
        author = this.author,
        subject = this.subject,
        content = this.content
    )
