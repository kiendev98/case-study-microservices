package com.kien.microservices.composite.product

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.kien.api.event.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.IOException

typealias JsonString = String

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class EventBaseTests {

    @Autowired
    private lateinit var mapper: ObjectMapper

    protected fun Event<*, *>.withoutCreatedAt(): String =
        try {
            mapper.convertValue(this, ObjectNode::class.java).apply {
                remove("eventCreatedAt")
            }.toString()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    protected fun JsonString.withoutCreatedAt(): String =
        try {
            (mapper.readTree(this) as ObjectNode).apply {
                remove("eventCreatedAt")
            }.toString()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
}