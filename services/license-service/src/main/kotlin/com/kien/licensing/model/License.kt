package com.kien.licensing.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table
data class License(
    @Id val licenseId: Long?,
    @Column val organizationId: Long?,
    @Column val description: String,
    @Column val productName: String,
    @Column val licenseType: String,
    @Transient val organizationName: String? = null,
    @Transient val contactName: String? = null,
    @Transient val contactPhone: String? = null,
    @Transient val contactEmail: String? = null
) {

    @PersistenceConstructor
    private constructor(
        licenseId: Long?,
        organizationId: Long?,
        description: String,
        productName: String,
        licenseType: String,
    ) : this(licenseId, organizationId, description, productName, licenseType, null, null, null, null)

    operator fun plus(organization: Organization): License =
        copy(
            organizationName = organization.name,
            contactName = organization.contactName,
            contactPhone = organization.contactPhone,
            contactEmail = organization.contactEmail
        )
}