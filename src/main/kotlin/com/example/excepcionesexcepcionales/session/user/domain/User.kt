package com.example.excepcionesexcepcionales.session.user.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserError2
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserError
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserError.DomainError.CardStatusNotConfirmed
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserError.DomainError.DocumentsNotVerified
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserError.DomainError.UserAlreadyVerified
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserError.DomainError.UserIsIncomplete
import com.example.excepcionesexcepcionales.session.user.domain.CardStatus.CONFIRMED
import com.example.excepcionesexcepcionales.session.user.domain.CardStatus.PENDING
import com.example.excepcionesexcepcionales.session.user.domain.Status.INCOMPLETE
import com.example.excepcionesexcepcionales.session.user.domain.Status.PENDING_VERIFICATION
import com.example.excepcionesexcepcionales.session.user.domain.Status.VERIFIED
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

    fun verify(): Either<VerifyUserError, User> = either {
        guardAllDocumentsAreVerified().bind()

        guardStatusCanBeVerified().bind()
        guardCardStatusToBeVerified().bind()

        copy(status = VERIFIED)
            .also { it.pushEvent(UserVerifiedEvent(id.toString())) }
    }

    private fun guardAllDocumentsAreVerified(): Either<VerifyUserError, Unit> =
        if (documents.all { document -> document.status == DocumentStatus.VERIFIED }) Unit.right()
        else DocumentsNotVerified.left()

    private fun guardStatusCanBeVerified(): Either<VerifyUserError, Unit> =
        when(status) {
            INCOMPLETE -> UserIsIncomplete.left()
            VERIFIED -> UserAlreadyVerified.left()
            PENDING_VERIFICATION -> Unit.right()
        }

    private fun guardCardStatusToBeVerified(): Either<VerifyUserError, Unit> =
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

sealed interface VerifyProfileDomainError {
    object UserIsIncomplete : VerifyProfileDomainError
    object CardStatusNotConfirmed : VerifyProfileDomainError
    object UserAlreadyVerified : VerifyProfileDomainError
    object DocumentsNotVerified : VerifyProfileDomainError
}



