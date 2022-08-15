package com.kien.microservices.core.product.services

import com.kien.api.core.product.Product
import com.kien.api.core.product.ProductService
import com.kien.api.exceptions.InvalidInputException
import com.kien.api.exceptions.NotFoundException
import com.kien.microservices.core.product.persistence.ProductRepository
import com.kien.util.http.ServiceUtil
import com.kien.util.logs.logWithClass
import org.springframework.dao.DuplicateKeyException
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.logging.Level

private val LOG = logWithClass<ProductServiceImpl>()

@RestController
class ProductServiceImpl(
    private val serviceUtil: ServiceUtil,
    private val repository: ProductRepository
) : ProductService {

    override fun createProduct(body: Product): Mono<Product> =
        if (body.productId < 1) {
            throw InvalidInputException("Invalid productId: ${body.productId}")
        } else {
            body.toEntity()
                .let { it -> repository.save(it) }
                .log(LOG.name, Level.FINE)
                .onErrorMap(DuplicateKeyException::class.java) { InvalidInputException("Duplicate key, Product Id: ${body.productId}") }
                .map { it.toApi() }
        }

    override fun deleteProduct(productId: Int): Mono<Void> =
        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        } else {
            LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId)
            repository.findByProductId(productId)
                .log(LOG.name, Level.FINE)
                .map { repository.delete(it) }
                .flatMap { it }
        }

    override fun getProduct(productId: Int): Mono<Product> =
        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        } else {
            LOG.info("Will get product info for id={}", productId)
            repository.findByProductId(productId)
                .switchIfEmpty(NotFoundException("No product found for productId: $productId").toMono())
                .log(LOG.name, Level.FINE)
                .map { it.toApi() }
                .map {
                    it.serviceAddress = serviceUtil.serviceAddress
                    it
                }
        }
}