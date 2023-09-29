package com.example.excepcionesexcepcionales.solution.user.mothers

import com.example.excepcionesexcepcionales.solution.user.domain.User
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.CreateUserRequestBody

object CreateUserRequestBodyMother {

    fun fromUser(user: User) =
        with(user) {
            CreateUserRequestBody(
                email = email.value,
                phoneNumber = phoneNumber.number(),
                phonePrefix = phoneNumber.prefix(),
                name = name.value,
                surname = surname.value
            )
        }
}
