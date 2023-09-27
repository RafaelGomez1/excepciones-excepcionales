package com.example.excepcionesexcepcionales.solution.user.mothers

import com.example.excepcionesexcepcionales.solution.user.application.create.imperative.ImperativeCreateUserCommand
import com.example.excepcionesexcepcionales.solution.user.domain.User

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
