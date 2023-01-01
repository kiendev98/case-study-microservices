package com.kien.microservices.core.product.config

import com.kien.api.core.product.Product
import com.kien.api.core.product.ProductService
import com.kien.api.event.Event
import com.kien.api.event.Type
import com.kien.api.exceptions.EventProcessingException
import com.kien.util.logs.logWithClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

private val LOG = logWithClass<MessageProcessorConfig>()

@Configuration
class MessageProcessorConfig(
    private val productService: ProductService
) {

    @Bean
    fun messageProcessor(): Consumer<Event<Int, Product>> = Consumer {
        LOG.info("Process message created at {}...", it.eventCreatedAt)

        when (it.eventType) {
            Type.CREATE -> {
                val product = it.data!!
                LOG.info("Create product with ID: {}", product.productId)
                productService.createProduct(product).block()
            }

            Type.DELETE -> {
                val productId = it.key
                LOG.info("Delete recommendations with ProductId: {}", productId)
                productService.deleteProduct(productId).block()
            }

            else -> {
                val errorMessage = "Incorrect event type: ${it.eventType}, expected a CREATE or DELETE event"
                LOG.warn(errorMessage)
                throw EventProcessingException(errorMessage)
            }
        }

        LOG.info("Message processing done!")
    }
}