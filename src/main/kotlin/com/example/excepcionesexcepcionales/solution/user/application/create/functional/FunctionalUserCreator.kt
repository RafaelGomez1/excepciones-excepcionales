package com.example.excepcionesexcepcionales.solution.user.application.create.functional

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.example.excepcionesexcepcionales.hive.user.domain.User
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.solution.user.application.create.functional.CreateUserError.UserAlreadyExists
import com.example.excepcionesexcepcionales.solution.user.domain.*
import com.example.excepcionesexcepcionales.solution.user.domain.ExistsUserCriteria.ByEmail
import java.time.ZonedDateTime

class FunctionalUserCreator(
    private val repository: SolutionUserRepository,
    private val publisher: DomainEventPublisher
) {

    fun invoke(
        id: UserId,
        email: Email,
        phoneNumber: PhoneNumber,
        createdOn: ZonedDateTime,
        name: Name,
        surname: Surname
    ): Either<CreateUserError, Unit> =
        guardUserAlreadyExists(email)
            .map { SolutionUser.create(id, email, phoneNumber, createdOn, name, surname) }
            .map { user -> save(user) }
            .map { user -> publisher.publish(user.pullEvents()) }

    fun invokeBlock(
        id: UserId,
        email: Email,
        phoneNumber: PhoneNumber,
        createdOn: ZonedDateTime,
        name: Name,
        surname: Surname
    ): Either<CreateUserError, Unit> = either {
        if (repository.exists(ByEmail(email))) UserAlreadyExists.left().bind()

        val user = SolutionUser.create(id, email, phoneNumber, createdOn, name, surname)

        repository.save(user)
        publisher.publish(user.pullEvents())
    }

    private fun guardUserAlreadyExists(email: Email) =
        if (repository.exists(ByEmail(email))) UserAlreadyExists.left()
        else Unit.right()

    private fun save(user: SolutionUser): SolutionUser {
        repository.save(user)
        return user
    }

}

sealed interface CreateUserError {
    object InvalidEmail : CreateUserError
    object InvalidName : CreateUserError
    object InvalidSurname : CreateUserError
    object InvalidMobilePhone : CreateUserError
    object UserAlreadyExists : CreateUserError
    class Unknown(val error: Throwable): CreateUserError
}
