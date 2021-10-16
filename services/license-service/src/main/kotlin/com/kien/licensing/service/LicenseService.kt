package com.kien.licensing.service

import com.kien.licensing.model.License
import com.kien.licensing.repository.LicenseRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class LicenseService(
    private val licenseRepository: LicenseRepository
) {
    fun getLicense(licenseId: Long, organizationId: Long): Mono<License> = licenseRepository.findByLicenseId(licenseId)

    fun updateLicense(license: License): Mono<License> = licenseRepository.save(license)

    fun deleteLicense(licenseId: Long): Mono<Void> = licenseRepository.deleteByLicenseId(licenseId)

    fun createLicense(license: License, organizationId: Long): Mono<License> =
        licenseRepository.save(
            license.copy(organizationId = organizationId)
        )
}