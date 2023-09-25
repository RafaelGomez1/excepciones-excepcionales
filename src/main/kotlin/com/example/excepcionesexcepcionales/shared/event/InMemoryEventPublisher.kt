package com.example.excepcionesexcepcionales.shared.event

class InMemoryEventPublisher : DomainEventPublisher {
    private val publishedEvents = mutableListOf<DomainEvent>()

    override fun <E : DomainEvent> publish(events: List<E>) {
        publishedEvents.addAll(events)
    }
}
