package com.example.excepcionesexcepcionales.session.user.mothers

import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserCommand
import com.example.excepcionesexcepcionales.session.user.domain.User

object VerifyUserCommandMother {

    fun fromUser(user: User) =
        VerifyUserCommand(user.id.toString())
}
