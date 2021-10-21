package com.kien.organization.controller

import com.kien.organization.model.Organization
import com.kien.organization.service.OrganizationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("v1/organization")
class OrganizationController(
    private val organizationService: OrganizationService
) {

    @GetMapping("/{organizationId}")
    fun getOrganization(@PathVariable organizationId: Long): Mono<ResponseEntity<Organization>> =
        organizationService.findById(organizationId)
            .map { ResponseEntity.ok(it) }


    @PutMapping("/{organizationId}")
    fun updateOrganization(
        @PathVariable organizationId: Long,
        @RequestBody organization: Organization
    ): Mono<Void> =
        organizationService.save(organization.copy(organizationId = organizationId)).then()


    @PostMapping
    fun saveOrganization(@RequestBody organization: Organization): Mono<ResponseEntity<Organization>> =
        organizationService.save(organization)
            .map { ResponseEntity.ok(it) }


    @DeleteMapping("/{organizationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteOrganization(@PathVariable organizationId: Long): Mono<Void> =
        organizationService.delete(organizationId)
}