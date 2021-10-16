package com.kien.licensing.repository

import com.kien.licensing.model.License
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface LicenseRepository: ReactiveCrudRepository<License, Long> {
    fun findByOrganizationId(organizationId: Long): Flux<License>
    fun findByOrganizationIdAndLicenseId(organizationId: Long, licenseId: Long): Mono<License>
    fun findByLicenseId(licenseId: Long): Mono<License>
    fun deleteByLicenseId(licenseId: Long): Mono<Void>
}