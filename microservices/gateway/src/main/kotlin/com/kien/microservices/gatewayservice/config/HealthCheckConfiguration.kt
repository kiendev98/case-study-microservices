package com.kien.microservices.gatewayservice.config

import com.kien.util.logs.logWithClass
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.ReactiveHealthContributor
import org.springframework.boot.actuate.health.ReactiveHealthIndicator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

val logger = logWithClass<HealthCheckConfiguration>()

@Configuration
class HealthCheckConfiguration(
    @Autowired private val webClientBuilder: WebClient.Builder,
) {

    private val webClient: WebClient = webClientBuilder.build()

    @Bean("microservices")
    fun healthCheckMicroservices(): ReactiveHealthContributor {
        val contributors = mapOf(
            "product" to getHealthContributor("http://product"),
            "recommendation" to getHealthContributor("http://recommendation"),
            "review" to getHealthContributor("http://review"),
            "product-composite" to getHealthContributor("http://product-composite"),
            "auth-server" to getHealthContributor("http://auth-server")
        )

        return CompositeReactiveHealthContributor.fromMap(contributors)
    }

    private fun getHealthContributor(url: String): ReactiveHealthContributor =
        ReactiveHealthIndicator {
            logger.debug("Will call the Health API on URL: {}", url)
            webClient.get()
                .uri("$url/actuator/health")
                .retrieve()
                .bodyToMono<String>()
                .map { Health.Builder().up().build() }
                .onErrorResume { Mono.just(Health.Builder().down(it).build()) }
                .log()
        }
}
