package com.kien.api.event

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer
import java.time.ZonedDateTime

enum class Type {
    CREATE,
    DELETE
}

data class Event<K, T>(
    val eventType: Type,
    val key: K,
    val data: T? = null,
    @JsonSerialize(using = ZonedDateTimeSerializer::class)
    val eventCreatedAt: ZonedDateTime = ZonedDateTime.now()
)