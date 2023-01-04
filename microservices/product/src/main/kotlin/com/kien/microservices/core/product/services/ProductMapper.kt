package com.kien.microservices.core.product.services

import com.kien.api.core.product.Product
import com.kien.microservices.core.product.persistence.ProductEntity

fun Product.toEntity(): ProductEntity =
    ProductEntity(
        productId = this.productId,
        name = this.name,
        weight = this.weight
    )

fun ProductEntity.toApi(): Product =
    Product(
        productId = this.productId,
        name = this.name,
        weight = this.weight,
    )
