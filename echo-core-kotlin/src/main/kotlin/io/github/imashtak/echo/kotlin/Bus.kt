package io.github.imashtak.echo.kotlin

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class Bus {

    private val channels: MutableMap<KClass<*>, MutableCollection<Channel<Event>>>

    val typeCount: Int
        get() = channels.size

    constructor() {
        channels = ConcurrentHashMap()
    }

    private fun registerType(type: KClass<*>) {
        if (!channels.containsKey(type)) {
            channels[type] = mutableListOf()
        }
    }

    private fun acquireChannel(type: KClass<*>): Channel<Event> {
        val channel = Channel<Event>()
        channels[type]?.add(channel)
        return channel
    }

    suspend inline fun <reified T : Any> publish(event: T) {
        publish(event, T::class)
    }

    suspend fun <T : Any> publish(event: T, type: KClass<T>) {
        if (!Event::class.isInstance(event)) {
            throw IllegalArgumentException()
        }
        registerType(type)
        for (key in channels.keys) {
            if (key.isInstance(event)) {
                for (channel in channels[type]!!) {
                    channel.send(event as Event)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <T : Any> subscribe(
        type: KClass<T>,
        action: (T) -> Unit,
        onException: (T, Throwable) -> Unit
    ) {
        registerType(type)
        for (event in acquireChannel(type)) {
            val e = event as T
            try {
                action(e)
            } catch (ex: Exception) {
                onException(e, ex)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun <T : Any> subscribeAsync(
        type: KClass<T>,
        action: (T) -> Unit,
        onException: (T, Throwable) -> Unit
    ) {
        GlobalScope.launch {
            subscribe(type, action, onException)
        }
    }
}