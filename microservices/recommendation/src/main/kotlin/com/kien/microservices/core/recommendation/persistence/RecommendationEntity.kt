package com.kien.microservices.core.recommendation.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "recommendations")
@CompoundIndex(name = "prod-rec-id", unique = true, def = "{'productId': 1, 'recommendationId' : 1}")
class RecommendationEntity(
    val productId: Int,
    val recommendationId: Int,
    var author: String,
    val rating: Int,
    val content: String
) {
    @Id
    var id: String? = null

    @Version
    var version: Int? = null
}