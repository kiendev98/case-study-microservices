package com.kien.api.core.review

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ReviewService {

    /**
     * Sample usage: "curl $HOST:$PORT/review?productId=1".
     *
     * @param productId Id of the product
     * @return the reviews of the product
     */
    @GetMapping(
        value = ["/review"],
        produces = ["application/json"]
    )
    fun getReviews(@RequestParam(value = "productId", required = true) productId: Int): Flux<Review>

    fun createReview(@RequestBody body: Review): Mono<Review>
    fun deleteReviews(@RequestParam(value = "productId", required = true) productId: Int): Mono<Void>
}
