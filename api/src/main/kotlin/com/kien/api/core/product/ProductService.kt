package com.kien.api.core.product

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

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
    fun getProduct(
        @PathVariable productId: Int,
        @RequestParam(value = "delay", required = false, defaultValue = "0") delay: Int,
        @RequestParam(value = "faultPercent", required = false, defaultValue = "0") faultPercent: Int
    ): Mono<Product>


    fun createProduct(@RequestBody body: Product): Mono<Product>

    fun deleteProduct(@PathVariable productId: Int): Mono<Void>
}