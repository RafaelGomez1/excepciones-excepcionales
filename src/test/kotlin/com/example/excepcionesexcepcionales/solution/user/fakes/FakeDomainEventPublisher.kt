package com.example.excepcionesexcepcionales.solution.user.fakes

import com.example.excepcionesexcepcionales.shared.event.DomainEvent
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import org.assertj.core.api.Assertions.assertThat

object FakeDomainEventPublisher : DomainEventPublisher {
    private val publishedEvents = mutableListOf<DomainEvent>()
    private val errors = mutableListOf<Throwable>()

    override fun <E : DomainEvent> publish(events: List<E>) {
        if (errors.isNotEmpty()) throw errors.removeFirst()
        publishedEvents.addAll(events)
    }

    @Deprecated("Use assertPublished")
    fun published(vararg event: DomainEvent) = publishedEvents.containsAll(event.toList())
    fun assertPublished(vararg event: DomainEvent) = assertThat(publishedEvents).contains(*event)

    fun assertPublished(assertions: DomainEvent.() -> Unit) = assertThat(publishedEvents).anySatisfy { it.assertions() }

    @Deprecated("Use assertNoEventsPublished")
    fun noEventsPublished() = publishedEvents.isEmpty()
    fun assertNoEventsPublished() = assertThat(publishedEvents).isEmpty()

    fun assertNotPublished(predicate: DomainEvent.() -> Boolean) = assertThat(publishedEvents).noneMatch { it.predicate() }

    fun resetFake() {
        publishedEvents.clear()
        errors.clear()
    }

    fun shouldFailWith(vararg errors: Throwable) = this.errors.addAll(errors)
}
