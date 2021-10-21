package com.kien.licensing.service.client

import com.kien.licensing.model.Organization
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class OrganizationDiscoveryClient(
    private val discoveryClient: DiscoveryClient,
    private val webClientBuilder: WebClient.Builder
) {

    fun getOrganization(organizationId: Long): Mono<Organization> =
        webClientBuilder
            .baseUrl("http://organization-service")
            .build()
            .get()
            .uri("/v1/organization/${organizationId}")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Organization::class.java)
}