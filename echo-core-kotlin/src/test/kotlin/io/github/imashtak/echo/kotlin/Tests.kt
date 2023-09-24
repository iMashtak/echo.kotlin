package io.github.imashtak.echo.kotlin

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals

class UnitTests {

    @Test
    fun testPublishSubscribe() {
        runBlocking {
            val bus = Bus()
            val counter = AtomicInteger()
            bus.subscribeAsync(TestEvent::class, { _ -> counter.incrementAndGet() }, { _, _ -> })
            bus.publish(TestEvent())
            delay(100)
            assertEquals(1, counter.get())
        }
    }

    @Test
    fun testPublishEventWithInterface() {
        runBlocking {
            val bus = Bus()
            val counter = AtomicInteger()
            bus.subscribeAsync(TestInterface::class, { _ -> counter.incrementAndGet() }, { _, _ -> })
            bus.subscribeAsync(TestEventWithInterface::class, { _ -> counter.incrementAndGet() }, { _, _ -> })
            val event = TestEventWithInterface() as TestInterface
            bus.publish(event)
            delay(100)
            assertEquals(2, counter.get())
        }
    }
}

class TestEvent : Event()

interface TestInterface

class TestEventWithInterface : Event(), TestInterface