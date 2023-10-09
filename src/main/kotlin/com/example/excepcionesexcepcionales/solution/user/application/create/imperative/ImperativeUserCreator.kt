package com.example.excepcionesexcepcionales.solution.user.application.create.imperative

import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.solution.user.domain.Email
import com.example.excepcionesexcepcionales.solution.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.solution.user.domain.Name
import com.example.excepcionesexcepcionales.solution.user.domain.Surname
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUser
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUser.UserAlreadyExistsException
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUserRepository
import java.time.ZonedDateTime

class ImperativeUserCreator(
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
    ) {
        guardUserExists(email)

        val user = SolutionUser.create(id, email, phoneNumber, createdOn, name, surname)

        repository.save(user)
        publisher.publish(user.pullEvents())
    }

    private fun guardUserExists(email: Email) {
        if (repository.existBy(email)) throw UserAlreadyExistsException()
    }
}
