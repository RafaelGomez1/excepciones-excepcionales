package com.example.excepcionesexcepcionales.solution.user.application.create.imperative

import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.solution.user.domain.Email
import com.example.excepcionesexcepcionales.solution.user.domain.Name
import com.example.excepcionesexcepcionales.solution.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.solution.user.domain.Surname
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import com.example.excepcionesexcepcionales.solution.user.domain.UserRepository
import java.time.ZonedDateTime
import java.util.UUID


class ImperativeCreateUserCommandHandler(
    repository: UserRepository,
    publisher: DomainEventPublisher
) {
    
    private val creator = ImperativeUserCreator(repository, publisher)
    
    fun handle(command: ImperativeCreateUserCommand) {
        with(command) {
            val name = Name.create(name)
            val surname = Surname.create(surname)
            val email = Email.create(email)
            val phoneNumber = PhoneNumber.create(phoneNumber, phonePrefix)

            creator.invoke(
                id = UserId(id),
                email = email,
                phoneNumber = phoneNumber,
                createdOn = createdOn,
                name = name,
                surname = surname
            )
        }
    }
}

data class ImperativeCreateUserCommand(
    val id: UUID,
    val email: String,
    val phonePrefix: String,
    val phoneNumber: String,
    val name: String,
    val surname: String,
    val createdOn: ZonedDateTime,
)
