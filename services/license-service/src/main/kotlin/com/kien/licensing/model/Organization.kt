package com.kien.licensing.model

data class Organization(
    val id: Long,
    val name: String,
    val contactName: String,
    val contactEmail: String,
    val contactPhone: String
)