package com.kien.api.core.review

import org.springframework.web.bind.annotation.*

interface ReviewService {

    /**
     * Sample usage, see below.
     *
     * curl -X POST $HOST:$PORT/review \
     * -H "Content-Type: application/json" --data \
     * '{"productId":123,"reviewId":456,"author":"me","subject":"yada, yada, yada","content":"yada, yada, yada"}'
     *
     * @param body A JSON representation of the new review
     * @return A JSON representation of the newly created review
     */
    @PostMapping(
        value = ["/review"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    fun createReview(@RequestBody body: Review): Review

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
    fun getReviews(@RequestParam(value = "productId", required = true) productId: Int): List<Review>

    /**
     * Sample usage: "curl -X DELETE $HOST:$PORT/review?productId=1".
     *
     * @param productId Id of the product
     */
    @DeleteMapping(value = ["/review"])
    fun deleteReviews(@RequestParam(value = "productId", required = true) productId: Int)
}