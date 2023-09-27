package com.example.excepcionesexcepcionales.solution.user.application.create.functional

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import com.example.excepcionesexcepcionales.shared.error.failIfTrue
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.shared.event.publishOrElse
import com.example.excepcionesexcepcionales.solution.user.application.create.functional.CreateUserError.Unknown
import com.example.excepcionesexcepcionales.solution.user.application.create.functional.CreateUserError.UserAlreadyExists
import com.example.excepcionesexcepcionales.solution.user.domain.Email
import com.example.excepcionesexcepcionales.solution.user.domain.ExistsUserCriteria.ByEmail
import com.example.excepcionesexcepcionales.solution.user.domain.Name
import com.example.excepcionesexcepcionales.solution.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.solution.user.domain.Surname
import com.example.excepcionesexcepcionales.solution.user.domain.User
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import com.example.excepcionesexcepcionales.solution.user.domain.UserRepository
import com.example.excepcionesexcepcionales.solution.user.domain.saveOrElse
import java.time.ZonedDateTime

class FunctionalUserCreator(
    private val repository: UserRepository,
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
        repository.exists(ByEmail(email))
            .failIfTrue { UserAlreadyExists }
            .map { User.create(id, email, phoneNumber, createdOn, name, surname) }
            .flatMap { user -> repository.saveOrElse(user) { error -> Unknown(error) } }
            .flatMap { user -> publisher.publishOrElse(user.pullEvents()) { error -> Unknown(error) } }

    fun invokeBlock(
        id: UserId,
        email: Email,
        phoneNumber: PhoneNumber,
        createdOn: ZonedDateTime,
        name: Name,
        surname: Surname
    ): Either<CreateUserError, Unit> = either {
        repository.exists(ByEmail(email)).failIfTrue { UserAlreadyExists }.bind()

        val user = User.create(id, email, phoneNumber, createdOn, name, surname)

        repository.saveOrElse(user) { error -> Unknown(error) }.bind()
        publisher.publishOrElse(user.pullEvents()) { error -> Unknown(error) }.bind()
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
