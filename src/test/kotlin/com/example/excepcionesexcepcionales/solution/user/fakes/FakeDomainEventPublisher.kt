package com.example.excepcionesexcepcionales.solution.user.fakes

import com.example.excepcionesexcepcionales.shared.event.DomainEvent
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher

object FakeDomainEventPublisher : DomainEventPublisher {
    private val elements = mutableListOf<DomainEvent>()

    override fun <E : DomainEvent> publish(events: List<E>) {
        elements.addAll(events)
    }

    fun resetFake() = elements.clear()
    fun wasPublished(resource: DomainEvent): Boolean = elements.contains(resource)
}
