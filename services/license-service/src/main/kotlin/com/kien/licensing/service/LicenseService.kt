package com.kien.licensing.service

import com.kien.licensing.model.License
import com.kien.licensing.repository.LicenseRepository
import com.kien.licensing.service.client.OrganizationDiscoveryClient
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.concurrent.TimeoutException
import kotlin.random.Random

@Service
class LicenseService(
    private val licenseRepository: LicenseRepository,
    private val organizationDiscoveryClient: OrganizationDiscoveryClient
) {

    private fun randomRunLong() {
        val randomNum = Random.nextInt(3) + 1
        sleep()
    }

    private fun sleep() {
        try {
            Thread.sleep(5000)
            throw TimeoutException()
        } catch (e: InterruptedException) {
            println(e.message)
        }
    }

    @CircuitBreaker(name = "licenseService")
    fun getLicense(licenseId: Long, organizationId: Long): Mono<License> =
        Mono.zip(
            licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId),
            organizationDiscoveryClient.getOrganization(organizationId)
        )
            .map {
                randomRunLong()
                it.t1 + it.t2
            }

    fun updateLicense(license: License): Mono<License> = licenseRepository.save(license)

    fun deleteLicense(licenseId: Long): Mono<Void> = licenseRepository.deleteByLicenseId(licenseId)

    fun createLicense(license: License, organizationId: Long): Mono<License> =
        licenseRepository.save(
            license.copy(organizationId = organizationId)
        )
}