package com.kien.microservices.core.product.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "products")
class ProductEntity(
    @Indexed(unique = true) var productId: Int,
    var name: String,
    var weight: Int,
) {
    @Id var id: String? = null
    @Version var version: Int? = null

    override fun hashCode(): Int = this::class.hashCode()
}