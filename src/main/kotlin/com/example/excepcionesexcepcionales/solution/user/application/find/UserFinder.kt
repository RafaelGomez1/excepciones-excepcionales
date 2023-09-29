package com.example.excepcionesexcepcionales.solution.user.application.find

import com.example.excepcionesexcepcionales.solution.user.domain.User
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import com.example.excepcionesexcepcionales.solution.user.domain.UserRepository

class UserFinder(private val repository: UserRepository) {

    fun invoke(id: UserId): User =
        if (!repository.existBy(id)) throw UserDoesNotExistException()
        else repository.findBy(id)
}

class UserDoesNotExistException : Throwable()
