package com.kien.licensing.config

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration


@Configuration
class WebConfig {

    @LoadBalanced
    @Bean
    fun webClientBuilder(): WebClient.Builder = WebClient.builder()

    @Bean
    fun defaultCustomizer(): Customizer<ReactiveResilience4JCircuitBreakerFactory> {
        return Customizer<ReactiveResilience4JCircuitBreakerFactory> { factory ->
            factory.configureDefault { id ->
                Resilience4JConfigBuilder(id)
                    .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                    .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build())
                    .build()
            }
        }
    }
}