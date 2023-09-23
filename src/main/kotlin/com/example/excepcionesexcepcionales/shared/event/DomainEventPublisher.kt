package com.example.excepcionesexcepcionales.shared.event

interface DomainEventPublisher {
    fun <E : DomainEvent> publish(event: E)
    fun <E : DomainEvent> publish(events: List<E>)
}

sealed interface DomainEvent {

    data class UserCreatedEvent(val id: String): DomainEvent
    data class UserVerifiedEvent(val id: String): DomainEvent
}
