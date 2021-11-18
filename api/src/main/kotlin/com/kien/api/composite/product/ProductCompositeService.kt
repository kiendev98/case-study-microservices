package com.kien.api.composite.product

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono


@Tag(name = "ProductComposite", description = "REST API for composite product information.")
interface ProductCompositeService {
    /**
     * Sample usage: "curl $HOST:$PORT/product-composite/1".
     *
     * @param productId Id of the product
     * @return the composite product info, if found, else null
     */
    @Operation(
        summary = "\${api.product-composite.get-composite-product.description}",
        description = "\${api.product-composite.get-composite-product.notes}"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "\${api.responseCodes.ok.description}"),
            ApiResponse(responseCode = "400", description = "\${api.responseCodes.badRequest.description}"),
            ApiResponse(responseCode = "404", description = "\${api.responseCodes.notFound.description}"),
            ApiResponse(responseCode = "422", description = "\${api.responseCodes.unprocessableEntity.description}")]
    )
    @GetMapping(value = ["/product-composite/{productId}"], produces = ["application/json"])
    fun getProduct(@PathVariable productId: Int): Mono<ProductAggregate>

    /**
     * Sample usage, see below.
     *
     * curl -X POST $HOST:$PORT/product-composite \
     *      -H "Content-Type: application/json" --data \
     *      '{"productId": 123, "name": "product 123", "weight": 123}'
     *
     * @param body A JSON representation of the new composite product
     */
    @Operation(
        summary = "\${api.product-composite.create-composite-product.description}",
        description = "\${api.product-composite.create-composite-product.notes}"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "400", description = "\${api.responseCodes.badRequest.description}"),
            ApiResponse(responseCode = "422", description = "\${api.responseCodes.unprocessableEntity.description}")
        ]
    )
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(
        value = ["/product-composite"],
        consumes = [APPLICATION_JSON_VALUE]
    )
    fun createProduct(@RequestBody body: ProductAggregate): Mono<Void>

    /**
     * Sample usage: "curl -X DELETE $HOST:$PORT/product-composite/1".
     *
     * @param productId Id of the product
     */
    @Operation(
        summary = "\${api.product-composite.delete-composite-product.description}",
        description = "\${api.product-composite.delete-composite-product.notes}"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "400", description = "\${api.responseCodes.badRequest.description}"),
            ApiResponse(responseCode = "422", description = "\${api.responseCodes.unprocessableEntity.description}")]
    )
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping(value = ["/product-composite/{productId}"])
    fun deleteProduct(@PathVariable productId: Int): Mono<Void>
}