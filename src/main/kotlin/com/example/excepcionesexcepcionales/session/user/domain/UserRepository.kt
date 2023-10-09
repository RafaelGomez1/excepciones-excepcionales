package com.example.excepcionesexcepcionales.session.user.domain

import com.example.excepcionesexcepcionales.session.user.domain.Email
import com.example.excepcionesexcepcionales.session.user.domain.User
import com.example.excepcionesexcepcionales.session.user.domain.UserId

interface UserRepository {
    fun findBy(userId: UserId): User
    fun existBy(userId: UserId): Boolean
    fun existBy(email: Email): Boolean
    fun save(user: User)
}
