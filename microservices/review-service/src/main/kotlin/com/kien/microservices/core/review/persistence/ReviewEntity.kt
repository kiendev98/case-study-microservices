package com.kien.microservices.core.review.persistence

import javax.persistence.*

@Entity
@Table(name = "reviews", indexes = [ Index(name = "reviews_unique_idx", unique = true, columnList = "productId,reviewId") ])
class ReviewEntity(
    val productId: Int,
    val reviewId: Int,
    var author: String,
    val subject: String,
    val content: String
) {
    @Id @GeneratedValue
    var id: Int? = null

    @Version
    var version: Int? = null
}