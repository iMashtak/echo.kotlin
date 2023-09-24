package io.github.imashtak.echo.kotlin

import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class Flow {

    private val id: UUID
    private val createdAt: Instant
    private val context: MutableMap<String, Any>

    constructor() {
        id = UUID.randomUUID()
        createdAt = Instant.now()
        context = ConcurrentHashMap()
    }

    constructor(context: MutableMap<String, Any>) {
        id = UUID.randomUUID()
        createdAt = Instant.now()
        this.context = context
    }
}

abstract class Event {

    private val id: UUID
    private val createdAt: Instant
    private val flow: Flow

    constructor() {
        id = UUID.randomUUID()
        createdAt = Instant.now()
        flow = Flow()
    }
}