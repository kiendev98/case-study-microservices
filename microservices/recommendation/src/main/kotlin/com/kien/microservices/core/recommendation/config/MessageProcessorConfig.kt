package com.kien.microservices.core.recommendation.config

import com.kien.api.core.recommendation.Recommendation
import com.kien.api.core.recommendation.RecommendationService
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
    private val recommendationService: RecommendationService
) {

    @Bean
    fun messageProcessor(): Consumer<Event<Int, Recommendation>> = Consumer {
        logger.info("Process message created at {}...", it.eventCreatedAt)

        when (it.eventType) {
            Type.CREATE -> {
                val recommendation: Recommendation = it.data!!
                logger.info(
                    "Create recommendation with ID: {}/{}",
                    recommendation.productId,
                    recommendation.recommendationId
                )
                recommendationService.createRecommendation(recommendation).block()
            }
            Type.DELETE -> {
                val productId: Int = it.key
                logger.info("Delete recommendations with ProductID: {}", productId)
                recommendationService.deleteRecommendations(productId).block()
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
