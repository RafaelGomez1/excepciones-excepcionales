package com.example.excepcionesexcepcionales.session.user.create.mothers

import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserCommand
import com.example.excepcionesexcepcionales.session.user.domain.User

object CreateUserCommandMother {

    fun fromUser(user: User = UserMother.random()) =
        with(user){
            CreateUserCommand(
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
