package com.kien.licensing.config

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class WebConfig {

    @LoadBalanced
    @Bean
    fun webClientBuilder(): WebClient.Builder = WebClient.builder()
}