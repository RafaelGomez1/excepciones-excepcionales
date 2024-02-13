package com.example.excepcionesexcepcionales.session.user.domain

import com.example.excepcionesexcepcionales.session.user.domain.CardStatus.CONFIRMED
import com.example.excepcionesexcepcionales.session.user.domain.CardStatus.PENDING
import com.example.excepcionesexcepcionales.session.user.domain.Status.*
import com.example.excepcionesexcepcionales.shared.event.Aggregate
import com.example.excepcionesexcepcionales.shared.event.DomainEvent.UserCreatedEvent
import com.example.excepcionesexcepcionales.shared.event.DomainEvent.UserVerifiedEvent
import java.time.ZonedDateTime

data class User(
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
        ) = User(id, email, phoneNumber, createdOn, name, surname, documents, status, cardStatus)
            .also { it.pushEvent(it.toUserCreatedEvent(id, email, phoneNumber, name, surname, createdOn, status, cardStatus))}
    }

    fun verify(): User {
        guardAllDocumentsAreVerified()
        guardStatusCanBeVerified()
        guardCardStatusToBeVerified()

        return copy(status = VERIFIED)
            .also { it.pushEvent(UserVerifiedEvent(id.toString())) }
    }

    private fun guardAllDocumentsAreVerified() {
        if (documents.all { document -> document.status == DocumentStatus.VERIFIED }) Unit
        if (documents.all { document -> document.status == DocumentStatus.VERIFIED }) Unit
        else throw NotAllDocumentVerifiedException()
    }

    private fun guardStatusCanBeVerified() {
        when (status) {
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

sealed interface VerifyProfileDomainError {
    object UserIsIncomplete : VerifyProfileDomainError
    object CardStatusNotConfirmed : VerifyProfileDomainError
    object UserAlreadyVerified : VerifyProfileDomainError
    object DocumentsNotVerified : VerifyProfileDomainError
}



