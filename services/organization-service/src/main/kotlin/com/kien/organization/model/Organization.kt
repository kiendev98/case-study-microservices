package com.kien.organization.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class Organization(
    @Id val organizationId: Long?,
    val name: String,
    val contactName: String,
    val contactEmail: String,
    val contactPhone: String
)