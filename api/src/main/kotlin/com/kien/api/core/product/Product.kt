package com.kien.api.core.product

data class Product(
    val productId: Int,
    val name: String,
    val weight: Int,
    val serviceAddress: String
)