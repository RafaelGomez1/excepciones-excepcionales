package com.example.excepcionesexcepcionales.shared.event

import arrow.core.Either
import arrow.core.Either.Companion.catch
import java.time.ZonedDateTime

interface DomainEventPublisher {
    fun <E : DomainEvent> publish(events: List<E>)
}

fun <Error> DomainEventPublisher.publishOrElse(
    events: List<DomainEvent>,
    onError: (Throwable) -> Error
): Either<Error, Unit> =
    catch { publish(events) }
        .mapLeft{ error -> onError(error) }

sealed interface DomainEvent {
    data class UserCreatedEvent(
        val id: String,
        val email: String,
        val mobilephonePrefix: String,
        val mobilephoneNumber: String,
        val name: String,
        val surname: String,
        val createdOn: ZonedDateTime,
        val status: String,
        val cardStatus: String
    ): DomainEvent

    data class UserEmailChangedEvent(val id: String, val email: String): DomainEvent
    data class UserVerifiedEvent(val id: String): DomainEvent
}
