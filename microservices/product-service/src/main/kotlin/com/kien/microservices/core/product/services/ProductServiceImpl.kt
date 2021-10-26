package com.kien.microservices.core.product.services

import com.kien.api.core.product.Product
import com.kien.api.core.product.ProductService
import com.kien.api.exceptions.InvalidInputException
import com.kien.api.exceptions.NotFoundException
import com.kien.api.log.logWithClass
import com.kien.util.http.ServiceUtil
import org.springframework.web.bind.annotation.RestController

private val LOG = logWithClass<ProductServiceImpl>()

@RestController
class ProductServiceImpl(
    private val serviceUtil: ServiceUtil
): ProductService {

    override fun getProduct(productId: Int): Product {
        LOG.debug("/product return the found product for productId={}", productId)

        return when {
            productId < 1 -> throw InvalidInputException("Invalid product id: $productId")
            productId == 13 ->  throw NotFoundException("No product found for productId: $productId")
            else -> Product(
                productId,
                "name-$productId",
                123,
                serviceUtil.serviceAddress
            )
        }
    }
}