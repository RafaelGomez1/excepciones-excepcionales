package com.example.excepcionesexcepcionales.hive.user.mothers

import com.example.excepcionesexcepcionales.hive.user.domain.Email
import com.example.excepcionesexcepcionales.hive.user.domain.Role
import com.example.excepcionesexcepcionales.hive.user.domain.User
import com.example.excepcionesexcepcionales.hive.user.infrastructure.controller.UserRequest

object UserMother {

    fun random(
        name: String = "Random Name",
        email: Email = Email("abc@gmail.com"),
        role: Role = Role.values().random()
    ) = User(name, email, role)
}

object UserRequestMother {

    fun fromUser(user: User) = UserRequest(user.name, user.email.value, user.role.name)
}
