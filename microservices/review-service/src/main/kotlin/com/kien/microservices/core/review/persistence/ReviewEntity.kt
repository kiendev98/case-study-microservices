package com.kien.microservices.core.review.persistence

import org.springframework.data.annotation.Version
import javax.persistence.*

@Entity
@Table(name = "reviews", indexes = [ Index(name = "reviews_unique_idx", unique = true, columnList = "productId,reviewId") ])
class ReviewEntity(
    @Id @GeneratedValue val id: Int?,
    @Version val version: Int,
    val productId: Int,
    val reviewId: Int,
    val author: String,
    val subject: String,
    val content: String
)