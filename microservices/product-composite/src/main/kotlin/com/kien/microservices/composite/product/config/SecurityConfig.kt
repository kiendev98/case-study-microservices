package com.kien.microservices.composite.product.config

import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod.*
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .authorizeExchange()
            .pathMatchers("/openapi/**").permitAll()
            .pathMatchers("/webjars/**").permitAll()
            .pathMatchers("/actuator/**").permitAll()
            .pathMatchers(POST, "/product-composite/**").hasAuthority(withScope("product:write"))
            .pathMatchers(DELETE, "/product-composite/**").hasAuthority(withScope("product:write"))
            .pathMatchers(GET, "/product-composite/**").hasAuthority(withScope("product:read"))
            .anyExchange().authenticated()
            .and()
            .oauth2ResourceServer()
            .jwt()

        return http.build()
    }

    private fun withScope(scope: String) = "SCOPE_$scope"
}
