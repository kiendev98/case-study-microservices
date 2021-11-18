package com.kien.microservices.composite.product

import com.fasterxml.jackson.core.JsonProcessingException
import com.kien.api.core.product.Product
import com.kien.api.event.Event
import com.kien.api.event.Type
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.json.shouldNotEqualJson
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.io.IOException

class IsSameEventTests : EventBaseTests() {

    @Throws(JsonProcessingException::class)
    @Test
    fun testEventObjectCompare() {
        // Event #1 and #2 are the same event, but occurs as different times
        // Event #3 and #4 are different events
        val event1 = Event(Type.CREATE, 1, Product(1, "name", 1, null))
        val event2 = Event(Type.CREATE, 1, Product(1, "name", 1, null))
        val event3 = Event(Type.DELETE, 1, null)
        val event4 = Event(Type.CREATE, 1, Product(2, "name", 1, null))

        event1.withoutCreatedAt() shouldEqualJson event2.withoutCreatedAt()
        event1.withoutCreatedAt() shouldNotEqualJson event3.withoutCreatedAt()
        event1.withoutCreatedAt() shouldNotEqualJson event4.withoutCreatedAt()
    }

}
