package com.example.excepcionesexcepcionales.solution.user.application.verify.functional

import arrow.core.Either
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUserRepository
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import java.util.UUID

class FunctionalVerifyUserCommandHandler(
    repository: SolutionUserRepository,
    publisher: DomainEventPublisher
) {
    private val verifyUser = FunctionalUserVerifier(repository, publisher)

    fun handle(command: FunctionalVerifyUserCommand): Either<FunctionalVerifyUserError, Unit> =
        verifyUser(UserId(command.userId))

}

data class FunctionalVerifyUserCommand(val userId: UUID)
