package com.kien.microservices.composite.product.config

import com.kien.microservices.composite.product.services.ProductCompositeIntegration
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor
import org.springframework.boot.actuate.health.ReactiveHealthContributor
import org.springframework.boot.actuate.health.ReactiveHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ActuatorConfig(
    private val integration: ProductCompositeIntegration
) {

    @Bean
    fun coreServices(): ReactiveHealthContributor =
        CompositeReactiveHealthContributor.fromMap(
            mapOf(
                "product" to ReactiveHealthIndicator { integration.productHealth },
                "recommendation" to ReactiveHealthIndicator { integration.recommendationHealth },
                "review" to ReactiveHealthIndicator { integration.reviewHealth }
            )
        )
}