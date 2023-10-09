package com.example.excepcionesexcepcionales.session.user.application.create

import com.example.excepcionesexcepcionales.session.user.domain.UserRepository
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.session.user.domain.Email
import com.example.excepcionesexcepcionales.session.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.session.user.domain.Name
import com.example.excepcionesexcepcionales.session.user.domain.Surname
import com.example.excepcionesexcepcionales.session.user.domain.UserId
import java.time.ZonedDateTime
import java.util.UUID

class CreateUserCommandHandler(
    repository: UserRepository,
    publisher: DomainEventPublisher
) {
    
    private val creator = UserCreator(repository, publisher)
    
    fun handle(command: CreateUserCommand) {
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

data class CreateUserCommand(
    val id: UUID,
    val email: String,
    val phonePrefix: String,
    val phoneNumber: String,
    val name: String,
    val surname: String,
    val createdOn: ZonedDateTime,
)
