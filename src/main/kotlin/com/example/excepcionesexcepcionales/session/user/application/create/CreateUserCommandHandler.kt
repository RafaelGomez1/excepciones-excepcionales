package com.example.excepcionesexcepcionales.session.user.application.create

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserError.InvalidEmail
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserError.InvalidName
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserError.InvalidPhoneNumber
import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserError.InvalidSurname
import com.example.excepcionesexcepcionales.session.user.domain.UserRepository
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.session.user.domain.Email
import com.example.excepcionesexcepcionales.session.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.session.user.domain.Name
import com.example.excepcionesexcepcionales.session.user.domain.Surname
import com.example.excepcionesexcepcionales.session.user.domain.UserId
import com.example.excepcionesexcepcionales.shared.validation.Validation
import com.example.excepcionesexcepcionales.shared.validation.Validation.Failure
import java.time.ZonedDateTime
import java.util.UUID

class CreateUserCommandHandler(
    repository: UserRepository,
    publisher: DomainEventPublisher
) {
    
    private val creator = UserCreator(repository, publisher)
    
    fun handle(command: CreateUserCommand): Either<CreateUserError, Unit> = either {
        with(command) {
            val name = Name.createOrElse(name) { InvalidName }.bind()
            val surname = Surname.createOrElse(surname) { InvalidSurname }.bind()
            val email = Email.createOrElse(email) { InvalidEmail }.bind()
            val phoneNumber = PhoneNumber.createOrElse(phoneNumber, phonePrefix) { InvalidPhoneNumber }.bind()

            creator.invoke(
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

data class CreateUserCommand(
    val id: UUID,
    val email: String,
    val phonePrefix: String,
    val phoneNumber: String,
    val name: String,
    val surname: String,
    val createdOn: ZonedDateTime,
)
