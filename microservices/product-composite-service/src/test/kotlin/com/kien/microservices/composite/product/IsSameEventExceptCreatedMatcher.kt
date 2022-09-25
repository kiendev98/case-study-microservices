package com.kien.microservices.composite.product

import com.kien.api.event.Event
import com.kien.util.logs.logWithClass
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

class IsSameEventExceptCreatedMatcher

private val LOG = logWithClass<IsSameEventExceptCreatedMatcher>()


infix fun Event<*,*>.isSameEventExceptCreated(expectedEvent: Event<*, *>) =
    this should haveSameEventExceptCreated(expectedEvent)

infix fun Event<*,*>.isNotSameEventExceptCreated(expectedEvent: Event<*, *>) =
    this shouldNot haveSameEventExceptCreated(expectedEvent)

private fun haveSameEventExceptCreated(expectedEvent: Event<*, *>) = Matcher<Event<*,*>>  {
    MatcherResult (
        it.match(expectedEvent),
        { "expected ${it.toJsonString()} to look like ${expectedEvent.toJsonString()}"},
        { "expected ${it.toJsonString()} values to not match ${expectedEvent.toJsonString()}" }
    )
}

private fun Event<*,*>.match(expectedEvent: Event<*, *>): Boolean {
    val eventAsJson = this.toJsonString()

    LOG.trace("Convert the following json string to a map: {}", eventAsJson)
    val mapEvent = eventAsJson.toMutableMap()
        .withoutCreatedAt()

    val mapExpectedEvent = expectedEvent.toMutableMap()
        .withoutCreatedAt()

    LOG.trace("Got the map: {}", mapEvent)
    LOG.trace("Compare to the expected map: {}", expectedEvent)
    return mapExpectedEvent == mapEvent
}


