package com.kien.licensing.model

import org.springframework.hateoas.RepresentationModel

data class License(
    val id: Int?,
    val organizationId: String?,
    val licenseId: String?,
    val description: String,
    val productName: String,
    val licenseType: String
): RepresentationModel<License>()