package com.example.excepcionesexcepcionales.solution.user.application.verify.functional

import arrow.core.Either
import arrow.core.raise.either
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.solution.user.application.verify.functional.FunctionalVerifyUserError.AggregateError
import com.example.excepcionesexcepcionales.solution.user.application.verify.functional.FunctionalVerifyUserError.UserNotFound
import com.example.excepcionesexcepcionales.solution.user.domain.FindUserCriteria.ById
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUser
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUserRepository
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import com.example.excepcionesexcepcionales.solution.user.domain.VerifyUserDomainError

class FunctionalUserVerifier(
    private val repository: SolutionUserRepository,
    private val publisher: DomainEventPublisher
) {

    operator fun invoke(userId: UserId): Either<FunctionalVerifyUserError, Unit> = either {
        val user = repository.find(ById(userId)) ?: raise(UserNotFound)

        val verifiedUser = verify(user).bind()

        repository.save(verifiedUser)
        publisher.publish(verifiedUser.pullEvents())
    }

    private fun verify(user: SolutionUser) =
        user.safeVerify().mapLeft { AggregateError(it) }
}

sealed class FunctionalVerifyUserError {
    object UserNotFound : FunctionalVerifyUserError()
    class AggregateError(val error: VerifyUserDomainError): FunctionalVerifyUserError()
}
