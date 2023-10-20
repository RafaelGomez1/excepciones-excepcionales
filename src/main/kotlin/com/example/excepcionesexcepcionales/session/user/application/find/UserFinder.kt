package com.example.excepcionesexcepcionales.session.user.application.find

import com.example.excepcionesexcepcionales.session.user.domain.UserRepository
import com.example.excepcionesexcepcionales.session.user.domain.User
import com.example.excepcionesexcepcionales.session.user.domain.UserId

class UserFinder(private val repository: UserRepository) {

    fun invoke(id: UserId): User =
        if (!repository.existByUserId(id)) throw UserDoesNotExistException()
        else repository.findBy(id)
}

class UserDoesNotExistException : RuntimeException()
