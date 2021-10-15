package com.kien.licensing.controller

import com.kien.licensing.model.License
import com.kien.licensing.service.LicenseService
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
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
        @PathVariable licenseId: String,
        @PathVariable organizationId: String
    ): Mono<License> =
        licenseService.getLicense(licenseId, organizationId)
            .map { license ->
                license.add(
                    linkTo(
                        methodOn(LicenseController::class.java)
                            .getLicense(licenseId, license.licenseId!!)
                    ).withSelfRel(),
                    linkTo(
                        methodOn(LicenseController::class.java)
                            .createLicense(organizationId, license)
                    ).withRel("createLicense"),
                    linkTo(
                        methodOn(LicenseController::class.java)
                            .updateLicense(organizationId, license)
                    ).withRel("updateLicense"),
                    linkTo(
                        methodOn(LicenseController::class.java)
                            .deleteLicense(license.licenseId!!, organizationId)
                    ).withRel("deleteLicense")
                )
                license
            }

    @PostMapping
    fun createLicense(
        @PathVariable organizationId: String,
        @RequestBody request: License
    ): Mono<ResponseEntity<String>> =
        licenseService.createLicense(request, organizationId)
            .map { ResponseEntity.ok(it) }

    @PutMapping
    fun updateLicense(
        @PathVariable("organizationId") organizationId: String,
        @RequestBody request: License
    ): Mono<ResponseEntity<String>> =
        licenseService.updateLicense(request, organizationId)
            .map { ResponseEntity.ok(it) }

    @DeleteMapping("/{licenseId}")
    fun deleteLicense(
        @PathVariable licenseId: String,
        @PathVariable organizationId: String
    ): Mono<ResponseEntity<String>> =
        licenseService.deleteLicense(licenseId, organizationId)
            .map { ResponseEntity.ok(it) }
}