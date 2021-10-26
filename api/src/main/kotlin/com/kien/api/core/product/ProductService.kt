package com.kien.api.core.product

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

interface ProductService {

    /**
     * Sample usage: "curl $HOST:$PORT/product/1
     *
     * @param productId Id of the product
     * @return the product
     */
    @GetMapping(
        value = ["/product/{productId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getProduct(@PathVariable productId: Int): Product
}