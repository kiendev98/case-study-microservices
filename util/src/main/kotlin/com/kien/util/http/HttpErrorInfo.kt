package com.kien.util.http

import org.springframework.http.HttpStatus
import java.time.ZonedDateTime

data class HttpErrorInfo(
    val timestamp: ZonedDateTime = ZonedDateTime.now(),
    val path: String,
    val httpStatus: HttpStatus,
    val message: String?
)
