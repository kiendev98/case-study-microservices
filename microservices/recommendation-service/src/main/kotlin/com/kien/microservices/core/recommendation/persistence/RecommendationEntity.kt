package com.kien.microservices.core.recommendation.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "recommendations")
@CompoundIndex(name = "prod-rec-id", unique = true, def = "{'productId': 1, 'recommendationId' : 1}")
class RecommendationEntity(
    @Id val id: String? = null,
    @Version val version: Int,
    val productId: Int,
    val recommendationId: Int,
    val author: String,
    val rating: Int,
    val content: String
)