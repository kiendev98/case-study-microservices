package com.kien.licensing.service

import com.kien.licensing.model.License
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import kotlin.random.Random

@Service
class LicenseService {
    fun getLicense(licenseId: String, organizationId: String): Mono<License> =
        Mono.just(
            License(
                id = Random.nextInt(1000),
                licenseId = licenseId,
                organizationId = organizationId,
                description = "Software product",
                productName = "Ostock",
                licenseType = "full"
            )
        )

    fun createLicense(license: License, organizationId: String): Mono<String> =
        Mono.just("This is a post and the object is: ${license.copy(organizationId = organizationId)}")

    fun updateLicense(license: License, organizationId: String): Mono<String> =
        Mono.just("This is a put and the object is: ${license.copy(organizationId = organizationId)}")

    fun deleteLicense(licenseId: String, organizationId: String): Mono<String> =
        Mono.just("Deleting license with id $licenseId for organization $organizationId")
}