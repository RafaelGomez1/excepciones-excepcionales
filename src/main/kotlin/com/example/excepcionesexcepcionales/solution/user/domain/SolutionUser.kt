package com.example.excepcionesexcepcionales.solution.user.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.example.excepcionesexcepcionales.shared.event.Aggregate
import com.example.excepcionesexcepcionales.shared.event.DomainEvent.UserCreatedEvent
import com.example.excepcionesexcepcionales.shared.event.DomainEvent.UserVerifiedEvent
import com.example.excepcionesexcepcionales.solution.user.domain.CardStatus.CONFIRMED
import com.example.excepcionesexcepcionales.solution.user.domain.CardStatus.PENDING
import com.example.excepcionesexcepcionales.solution.user.domain.Status.*
import com.example.excepcionesexcepcionales.solution.user.domain.VerifyUserDomainError.*
import java.time.ZonedDateTime

data class SolutionUser(
    val id: UserId,
    val email: Email,
    val phoneNumber: PhoneNumber,
    val createdOn: ZonedDateTime,
    val name: Name,
    val surname: Surname,
    val documents: Set<Document>,
    val status: Status,
    val cardStatus: CardStatus
): Aggregate() {

    companion object {
        fun create(
            id: UserId,
            email: Email,
            phoneNumber: PhoneNumber,
            createdOn: ZonedDateTime,
            name: Name,
            surname: Surname,
            documents: Set<Document> = emptySet(),
            status: Status = INCOMPLETE,
            cardStatus: CardStatus = PENDING
        ) = SolutionUser(id, email, phoneNumber, createdOn, name, surname, documents, status, cardStatus)
            .also {
                it.pushEvent(it.toUserCreatedEvent(id, email, phoneNumber, name, surname, createdOn, status, cardStatus))
            }
    }

    fun verify(): SolutionUser {
        guardAllDocumentsAreVerified()
        guardStatusCanBeVerified()
        guardCardStatusToBeVerified()

        return copy(status = VERIFIED)
            .also { it.pushEvent(UserVerifiedEvent(id.toString())) }
    }

    private fun guardAllDocumentsAreVerified() =
        if (documents.all { document -> document.status == DocumentStatus.VERIFIED }) Unit
        else throw NotAllDocumentVerifiedException()

    private fun guardStatusCanBeVerified() {
        when(status) {
            INCOMPLETE -> throw UserStatusCannotBeVerifiedException()
            VERIFIED -> throw UserAlreadyVerifiedException()
            PENDING_VERIFICATION -> Unit
        }
    }

    private fun guardCardStatusToBeVerified() {
        when(cardStatus) {
            PENDING -> throw CardStatusNotConfirmedException()
            CONFIRMED -> Unit
        }
    }

    fun safeVerify(): Either<VerifyUserDomainError, SolutionUser> = either {
        guardAllDocumentsAreVerifiedEither().bind()
        guardStatusCanBeVerifiedEither().bind()
        guardCardStatusToBeVerifiedEither().bind()

        return copy(status = VERIFIED)
            .also { it.pushEvent(UserVerifiedEvent(id.toString())) }
            .right()
    }

    private fun guardAllDocumentsAreVerifiedEither() =
        if(documents.all { document -> document.status == DocumentStatus.VERIFIED }) Unit.right()
        else NotAllDocumentVerified.left()

    private fun guardStatusCanBeVerifiedEither() =
        when(status) {
            INCOMPLETE -> UserStatusCannotBeVerified.left()
            VERIFIED -> UserAlreadyVerified.left()
            PENDING_VERIFICATION -> Unit.right()
        }

    private fun guardCardStatusToBeVerifiedEither() =
        when(cardStatus) {
            PENDING -> CardStatusNotConfirmed.left()
            CONFIRMED -> Unit.right()
        }

    private fun toUserCreatedEvent(
        id: UserId,
        email: Email,
        phoneNumber: PhoneNumber,
        name: Name,
        surname: Surname,
        createdOn: ZonedDateTime,
        status: Status,
        cardStatus: CardStatus
    ) = UserCreatedEvent(
        id = id.toString(),
        email = email.value,
        mobilephonePrefix = phoneNumber.prefix(),
        mobilephoneNumber = phoneNumber.number(),
        name = name.value,
        surname = surname.value,
        createdOn = createdOn,
        status = status.name,
        cardStatus = cardStatus.name
    )

    class NotAllDocumentVerifiedException() : RuntimeException()
    class UserAlreadyVerifiedException() : RuntimeException()
    class UserStatusCannotBeVerifiedException() : RuntimeException()
    class CardStatusNotConfirmedException() : RuntimeException()
    class CannotChangeEmailToTheSameValueException() : RuntimeException()
    class UserAlreadyExistsException() : RuntimeException()
}

sealed class VerifyUserDomainError {
    object NotAllDocumentVerified : VerifyUserDomainError()
    object UserAlreadyVerified : VerifyUserDomainError()
    object UserStatusCannotBeVerified : VerifyUserDomainError()
    object CardStatusNotConfirmed : VerifyUserDomainError()
}


