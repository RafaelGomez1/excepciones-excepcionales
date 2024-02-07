package com.example.excepcionesexcepcionales.session.user.application.verify

import arrow.core.Either
import com.example.excepcionesexcepcionales.session.user.domain.UserId
import com.example.excepcionesexcepcionales.session.user.domain.UserRepository
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher

class VerifyUserCommandHandler(
    repository: UserRepository,
    publisher: DomainEventPublisher
) {
    private val verifier = UserVerifier(repository, publisher)

    fun handle(command: VerifyUserCommand): Either<VerifyUserError, Unit> =
        verifier.invoke(UserId.fromString(command.userId))
}

data class VerifyUserCommand(val userId: String)
