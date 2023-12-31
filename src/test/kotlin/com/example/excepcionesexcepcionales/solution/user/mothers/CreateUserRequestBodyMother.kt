package com.example.excepcionesexcepcionales.solution.user.mothers

import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUser
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.CreateUserRequestBody

object CreateUserRequestBodyMother {

    fun fromUser(user: SolutionUser) =
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
