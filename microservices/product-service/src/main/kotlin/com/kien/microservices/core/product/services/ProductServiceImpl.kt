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

private val LOG = logWithClass<ProductServiceImpl>()

@RestController
class ProductServiceImpl(
    private val serviceUtil: ServiceUtil,
    private val repository: ProductRepository
) : ProductService {

    override fun createProduct(body: Product): Product = try {
        body.apiToEntity()
            .let { repository.save(it) }
            .apply {
                LOG.debug("createProduct: entity created for productId: {}", productId)
            }
            .let { it.entityToApi() }
    } catch (ex: DuplicateKeyException) {
        throw InvalidInputException("Duplicate key, Product Id: ${body.productId}")
    }

    override fun deleteProduct(productId: Int) {
        LOG.debug("delete product: tries to delete an entity with productId: {}", productId)
        repository.findByProductId(productId)?.let {
            repository.delete(it)
        }
    }

    override fun getProduct(productId: Int): Product =
        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        } else {
            repository.findByProductId(productId)
                ?.let { it.entityToApi() }
                ?.apply { serviceAddress = serviceUtil.serviceAddress }
                ?.apply { LOG.debug("getProduct: found productId: {}", productId) }
                ?: throw NotFoundException("No product found for productId: $productId")
        }
}