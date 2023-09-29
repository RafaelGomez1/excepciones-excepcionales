package com.example.excepcionesexcepcionales.solution.user.application.verify.imperative

import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import com.example.excepcionesexcepcionales.solution.user.domain.UserRepository
import java.util.UUID

class ImperativeVerifyUserCommandHandler(
    repository: UserRepository,
    publisher: DomainEventPublisher
) {

    private val verifier = ImperativeUserVerifier(repository, publisher)

    fun handle(command: ImperativeVerifyUserCommand) {
        verifier.invoke(UserId(command.userId))
    }
}

data class ImperativeVerifyUserCommand(val userId: UUID)
