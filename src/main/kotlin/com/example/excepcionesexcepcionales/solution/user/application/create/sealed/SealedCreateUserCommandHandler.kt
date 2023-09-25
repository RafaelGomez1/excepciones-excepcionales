package com.example.excepcionesexcepcionales.solution.user.application.create.sealed

import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.shared.validation.Validation.Failure
import com.example.excepcionesexcepcionales.shared.validation.Validation.Success
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.InvalidEmail
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.InvalidMobilePhone
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.InvalidName
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.CreateUserResult.InvalidSurname
import com.example.excepcionesexcepcionales.solution.user.domain.Email
import com.example.excepcionesexcepcionales.solution.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.solution.user.domain.Name
import com.example.excepcionesexcepcionales.solution.user.domain.Surname
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import com.example.excepcionesexcepcionales.solution.user.domain.UserRepository
import java.time.ZonedDateTime
import java.util.UUID

class SealedCreateUserCommandHandler(
    repository: UserRepository,
    publisher: DomainEventPublisher
) {

    private val creator = SealedUserCreator(repository, publisher)

    fun handle(command: SealedCreateUserCommand): CreateUserResult {
        val nameResult = Name.validated(command.name)
        val surnameResult = Surname.validated(command.surname)
        val emailResult = Email.validated(command.email)
        val mobileResult = PhoneNumber.validated(number = command.phoneNumber, prefix = command.phonePrefix)

        if(nameResult is Failure) return InvalidName
        if(surnameResult is Failure) return InvalidSurname
        if(emailResult is Failure) return InvalidEmail
        if(mobileResult is Failure) return InvalidMobilePhone

        return creator.invoke(
            id = UserId(command.id),
            email = (emailResult as Success).value,
            phoneNumber = (mobileResult as Success).value,
            createdOn = command.createdOn,
            name = (nameResult as Success).value,
            surname = (surnameResult as Success).value
        )
    }
}

data class SealedCreateUserCommand(
    val id: UUID,
    val email: String,
    val phonePrefix: String,
    val phoneNumber: String,
    val name: String,
    val surname: String,
    val createdOn: ZonedDateTime,
)
