package com.example.excepcionesexcepcionales.session.user.create.mothers

import com.example.excepcionesexcepcionales.session.user.domain.User
import com.example.excepcionesexcepcionales.solution.user.application.create.imperative.ImperativeCreateUserCommand

object ImperativeCreateUserCommandMother {

    fun fromUser(user: User = UserMother.random()) =
        with(user){
            ImperativeCreateUserCommand(
                id = user.id.value,
                email = email.value,
                phoneNumber = phoneNumber.number(),
                phonePrefix = phoneNumber.prefix(),
                name = name.value,
                surname = surname.value,
                createdOn = user.createdOn
            )
        }
}
