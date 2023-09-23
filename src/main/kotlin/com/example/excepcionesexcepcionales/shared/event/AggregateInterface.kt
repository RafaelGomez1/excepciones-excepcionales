package com.example.excepcionesexcepcionales.shared.event

interface AggregateInterface {
    fun pullEvents(): List<DomainEvent>
    fun <E : DomainEvent> pushEvent(event: E)
}

open class Aggregate : AggregateInterface {
    private var events: MutableList<DomainEvent> = mutableListOf()

    override fun pullEvents(): List<DomainEvent> {
        val pulledEvents: MutableList<DomainEvent> = events
        events = mutableListOf()
        return pulledEvents.toList()
    }

    override fun <E : DomainEvent> pushEvent(event: E) {
        this.events.add(event)
    }
}
