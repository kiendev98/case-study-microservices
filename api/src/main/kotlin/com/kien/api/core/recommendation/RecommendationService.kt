package com.kien.api.core.recommendation

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RecommendationService {

    /**
     * Sample usage: "curl $HOST:$PORT/recommendation?productId=1".
     *
     * @param productId Id of the product
     * @return the recommendations of the product
     */
    @GetMapping(
        value = ["/recommendation"],
        produces = ["application/json"]
    )
    fun getRecommendations(
        @RequestParam(value = "productId", required = true) productId: Int
    ): Flux<Recommendation>

    fun createRecommendation(@RequestBody body: Recommendation): Mono<Recommendation>
    fun deleteRecommendations(@RequestParam(value = "productId", required = true) productId: Int): Mono<Void>
}
