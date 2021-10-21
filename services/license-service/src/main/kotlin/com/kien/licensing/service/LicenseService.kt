package com.kien.licensing.service

import com.kien.licensing.model.License
import com.kien.licensing.repository.LicenseRepository
import com.kien.licensing.service.client.OrganizationDiscoveryClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class LicenseService(
    private val licenseRepository: LicenseRepository,
    private val organizationDiscoveryClient: OrganizationDiscoveryClient
) {
    fun getLicense(licenseId: Long, organizationId: Long): Mono<License> =
        Mono.zip(
            licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId),
            organizationDiscoveryClient.getOrganization(organizationId)
        ).map { it.t1 + it.t2 }

    fun updateLicense(license: License): Mono<License> = licenseRepository.save(license)

    fun deleteLicense(licenseId: Long): Mono<Void> = licenseRepository.deleteByLicenseId(licenseId)

    fun createLicense(license: License, organizationId: Long): Mono<License> =
        licenseRepository.save(
            license.copy(organizationId = organizationId)
        )
}