package com.example.excepcionesexcepcionales.session.user.application.verify

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserError.UserDoesNotExist
import com.example.excepcionesexcepcionales.session.user.domain.FindUserCriteria.ById
import com.example.excepcionesexcepcionales.session.user.domain.UserId
import com.example.excepcionesexcepcionales.session.user.domain.UserRepository
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher

class UserVerifier(
    private val repository: UserRepository,
    private val publisher: DomainEventPublisher
) {

    fun invoke(id: UserId): Either<VerifyUserError, Unit> = either {
        val user = repository.find(ById(id)) ?: UserDoesNotExist.left().bind()

        val verifiedUser = user.verify().bind()

        repository.save(verifiedUser)
        publisher.publish(verifiedUser.pullEvents())
    }
}

sealed interface VerifyUserError {
    object UserDoesNotExist : VerifyUserError
    object InvalidIdentifier : VerifyUserError

    sealed interface DomainError : VerifyUserError {
        object UserIsIncomplete : DomainError
        object CardStatusNotConfirmed : DomainError
        object UserAlreadyVerified : DomainError
        object DocumentsNotVerified : DomainError
    }
}
