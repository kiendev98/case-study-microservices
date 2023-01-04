package com.kien.microservices.composite.product

import com.fasterxml.jackson.core.JsonProcessingException
import com.kien.api.core.product.Product
import com.kien.api.event.Event
import com.kien.api.event.Type
import org.junit.jupiter.api.Test

class IsSameEventTests {

    @Throws(JsonProcessingException::class)
    @Test
    fun `test event object compare`() {
        // Event #1 and #2 are the same event, but occurs as different times
        // Event #3 and #4 are different events
        val event1 = Event(Type.CREATE, 1, Product(1, "name", 1, null))
        val event2 = Event(Type.CREATE, 1, Product(1, "name", 1, null))
        val event3 = Event(Type.DELETE, 1, null)
        val event4 = Event(Type.CREATE, 1, Product(2, "name", 1, null))

        event1 isSameEventExceptCreated event2
        event1 isNotSameEventExceptCreated event3
        event1 isNotSameEventExceptCreated event4
    }
}
