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

private val logger = logWithClass<MessageProcessorConfig>()

@Configuration
class MessageProcessorConfig(
    private val productService: ProductService
) {

    @Bean
    fun messageProcessor(): Consumer<Event<Int, Product>> = Consumer {
        logger.info("Process message created at {}...", it.eventCreatedAt)

        when (it.eventType) {
            Type.CREATE -> {
                val product = it.data!!
                logger.info("Create product with ID: {}", product.productId)
                productService.createProduct(product).block()
            }

            Type.DELETE -> {
                val productId = it.key
                logger.info("Delete recommendations with ProductId: {}", productId)
                productService.deleteProduct(productId).block()
            }

            else -> {
                val errorMessage = "Incorrect event type: ${it.eventType}, expected a CREATE or DELETE event"
                logger.warn(errorMessage)
                throw EventProcessingException(errorMessage)
            }
        }

        logger.info("Message processing done!")
    }
}
