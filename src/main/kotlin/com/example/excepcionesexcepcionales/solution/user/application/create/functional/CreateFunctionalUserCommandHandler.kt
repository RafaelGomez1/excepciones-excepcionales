package com.example.excepcionesexcepcionales.solution.user.application.create.functional

import arrow.core.Either
import arrow.core.raise.either
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.solution.user.application.create.functional.CreateUserError.InvalidEmail
import com.example.excepcionesexcepcionales.solution.user.application.create.functional.CreateUserError.InvalidMobilePhone
import com.example.excepcionesexcepcionales.solution.user.application.create.functional.CreateUserError.InvalidName
import com.example.excepcionesexcepcionales.solution.user.application.create.functional.CreateUserError.InvalidSurname
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.SealedUserCreator
import com.example.excepcionesexcepcionales.solution.user.domain.Email
import com.example.excepcionesexcepcionales.solution.user.domain.Name
import com.example.excepcionesexcepcionales.solution.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.solution.user.domain.Surname
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import com.example.excepcionesexcepcionales.solution.user.domain.UserRepository
import java.time.ZonedDateTime
import java.util.UUID

class CreateFunctionalUserCommandHandler(
    repository: UserRepository,
    publisher: DomainEventPublisher
) {

    private val creator = FunctionalUserCreator(repository, publisher)
    
    fun handle(command: CreateFunctionalUserCommand): Either<CreateUserError, Unit> = either {
        with(command) {
            val name = Name.createOrElse(name) { InvalidName }.bind()
            val surname = Surname.createOrElse(surname) { InvalidSurname }.bind()
            val email = Email.createOrElse(email) { InvalidEmail }.bind()
            val phoneNumber = PhoneNumber.createOrElse(phoneNumber, phonePrefix) { InvalidMobilePhone }.bind()

            creator.invokeBlock(
                id = UserId(id),
                email = email,
                phoneNumber = phoneNumber,
                createdOn = createdOn,
                name = name,
                surname = surname
            ).bind()
        }
    }
}

data class CreateFunctionalUserCommand(
    val id: UUID,
    val email: String,
    val phonePrefix: String,
    val phoneNumber: String,
    val name: String,
    val surname: String,
    val createdOn: ZonedDateTime,
)
