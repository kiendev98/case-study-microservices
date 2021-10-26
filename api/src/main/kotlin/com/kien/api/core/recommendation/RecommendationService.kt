package com.kien.api.core.recommendation

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

interface RecommendationService {

    /**
     * Sample usage: "curl $HOST:PORT/recommendation?productId=1"
     */
    @GetMapping(
        value = ["/recommendation"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getRecommendations(
        @RequestParam(value = "productId", required = true) productId: Int
    ): List<Recommendation>
}