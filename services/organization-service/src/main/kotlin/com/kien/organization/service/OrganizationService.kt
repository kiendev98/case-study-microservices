package com.kien.organization.service

import com.kien.organization.model.Organization
import com.kien.organization.repository.OrganizationRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class OrganizationService(
    private val repository: OrganizationRepository
) {

    fun findById(organizationId: Long): Mono<Organization> =
        repository.findById(organizationId)

    fun save(organization: Organization): Mono<Organization> =
        repository.save(organization)

    fun delete(organizationId: Long): Mono<Void> =
        repository.deleteById(organizationId)
}