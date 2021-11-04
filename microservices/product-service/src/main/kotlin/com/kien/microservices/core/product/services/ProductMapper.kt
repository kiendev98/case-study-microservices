package com.kien.microservices.core.product.services

import com.kien.api.core.product.Product
import com.kien.microservices.core.product.persistence.ProductEntity

fun Product.apiToEntity(): ProductEntity =
    ProductEntity(
        productId = this.productId,
        name = this.name,
        weight = this.weight
    )

fun ProductEntity.entityToApi(): Product =
    Product(
        productId = this.productId,
        name = this.name,
        weight = this.weight,
    )