package com.example.excepcionesexcepcionales.session.user.mothers

import com.example.excepcionesexcepcionales.session.user.domain.User
import com.example.excepcionesexcepcionales.shared.event.DomainEvent.UserCreatedEvent
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUser

object UserCreatedEventMother {

    fun fromUser(user: User) =
        with(user) {
            UserCreatedEvent(
                id = id.value.toString(),
                email = email.value,
                mobilephonePrefix = phoneNumber.prefix(),
                mobilephoneNumber = phoneNumber.number(),
                name = name.value,
                surname = surname.value,
                createdOn = createdOn,
                status = status.name,
                cardStatus = cardStatus.name
            )
        }

}
