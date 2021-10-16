package com.kien.licensing.controller

import com.kien.licensing.model.License
import com.kien.licensing.service.LicenseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("v1/organization/{organizationId}/license")
class LicenseController(
    private val licenseService: LicenseService
) {

    @GetMapping("/{licenseId}")
    fun getLicense(
        @PathVariable licenseId: Long,
        @PathVariable organizationId: Long
    ): Mono<License> =
        licenseService.getLicense(licenseId, organizationId)

    @PostMapping
    fun createLicense(
        @PathVariable organizationId: Long,
        @RequestBody request: License
    ): Mono<ResponseEntity<License>> =
        licenseService.createLicense(request, organizationId)
            .map { ResponseEntity.ok(it) }

    @PutMapping
    fun updateLicense(
        @PathVariable("organizationId") organizationId: Long,
        @RequestBody request: License
    ): Mono<ResponseEntity<License>> =
        licenseService.updateLicense(request)
            .map { ResponseEntity.ok(it) }

    @DeleteMapping("/{licenseId}")
    fun deleteLicense(
        @PathVariable licenseId: Long
    ): Mono<ResponseEntity<Void>> =
        licenseService.deleteLicense(licenseId)
            .map { ResponseEntity.ok().build() }
}