package com.kien.licensing.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class License(
    @Id val licenseId: Long?,
    val organizationId: Long?,
    val description: String,
    val productName: String,
    val licenseType: String
)