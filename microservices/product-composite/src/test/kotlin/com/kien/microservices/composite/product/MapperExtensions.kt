package com.kien.microservices.composite.product

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kien.api.event.Event
import java.io.IOException

typealias EventJsonString = String

/**
 * Serialize a json string to map
 */
fun EventJsonString.toMutableMap(): MutableMap<*, *> = try {
    jacksonObjectMapper().readValue(this)
} catch (ex: IOException) {
    throw RuntimeException(ex)
}

/**
 * Deserialize event object to map
 */
fun Event<*, *>.toMutableMap(): MutableMap<*, *> = jacksonObjectMapper().convertValue<JsonNode>(this).let {
    jacksonObjectMapper().convertValue(it)
}

fun Any.toJsonString(): String = try {
    jacksonObjectMapper().writeValueAsString(this)
} catch (ex: JsonProcessingException) {
    throw RuntimeException(ex)
}

/**
 *
 * Deserialize the event object to json string
 * and remove `eventCreatedAt` field
 */
fun Event<*, *>.withoutCreatedAt(): EventJsonString = this.toMutableMap().withoutCreatedAt().toJsonString()

/**
 * Remove the `eventCreatedAt` field of the json string
 */
fun EventJsonString.withoutCreatedAt(): EventJsonString = this.toMutableMap().apply {
    remove("eventCreatedAt")
}.toJsonString()

/**
 * Remove the `eventCreatedAt` key of the map
 */
fun MutableMap<*, *>.withoutCreatedAt() = apply {
    remove("eventCreatedAt")
}
