package com.kien.microservices.core.review.config

import com.kien.api.core.review.Review
import com.kien.api.core.review.ReviewService
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
    private val reviewService: ReviewService
) {

    @Bean
    fun messageProcessor(): Consumer<Event<Int, Review>> = Consumer {
        LOG.info("Process message created at {}...", it.eventCreatedAt)

        when (it.eventType) {
            Type.CREATE -> {
                val review: Review = it.data!!
                LOG.info(
                    "Create review with ID: {}/{}",
                    review.productId,
                    review.reviewId
                )
                reviewService.createReview(review).block()
            }
            Type.DELETE -> {
                val productId: Int = it.key
                LOG.info(
                    "Delete reviews with ProductID: {}",
                    productId
                )
                reviewService.deleteReviews(productId).block()
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