package com.example.excepcionesexcepcionales.user.application.find

import com.example.excepcionesexcepcionales.user.domain.User
import com.example.excepcionesexcepcionales.user.domain.UserId
import com.example.excepcionesexcepcionales.user.domain.UserRepository

class UserFinder(private val repository: UserRepository) {

    fun invoke(id: UserId): User =
        repository.findBy(id)
}
