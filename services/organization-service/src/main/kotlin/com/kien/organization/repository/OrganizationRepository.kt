package com.kien.organization.repository

import com.kien.organization.model.Organization
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrganizationRepository: ReactiveCrudRepository<Organization, Long>