package com.kien.api.core.review

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

interface ReviewService {

    /**
     * Sample usage: "curl $HOST:$PORT/review?productId=1"
     *
     * @param productId Id of the product
     * @return the reviews of the product
     */
    @GetMapping(
        value = ["/review"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getReviews(@RequestParam(value = "productId", required = true) productId: String): List<Review>
}