package io.github.imashtak.echo.kotlin

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals

class UnitTests {

    @Test
    fun testPublishSubscribe() {
        val bus = Bus()
        val counter = AtomicInteger()
        runBlocking {
            bus.subscribeAsync(TestEvent::class, { _ -> counter.incrementAndGet() }, { _, _ -> })
            bus.publish(TestEvent())
            delay(100)
        }
        assertEquals(1, counter.get())
    }
}

class TestEvent : Event() {

}