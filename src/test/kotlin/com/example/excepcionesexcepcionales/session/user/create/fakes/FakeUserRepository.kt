package com.example.excepcionesexcepcionales.session.user.create.fakes

import com.example.excepcionesexcepcionales.session.user.domain.Email
import com.example.excepcionesexcepcionales.session.user.domain.User
import com.example.excepcionesexcepcionales.session.user.domain.UserId
import com.example.excepcionesexcepcionales.session.user.domain.UserRepository
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeRepository

object FakeUserRepository : UserRepository, FakeRepository<User> {
    override val elements = mutableListOf<User>()

    override fun findBy(userId: UserId): User = elements.first { it.id == userId }

    override fun existBy(userId: UserId): Boolean = elements.any { it.id == userId }

    override fun existBy(email: Email): Boolean = elements.any { it.email == email }

    override fun save(user: User) { elements.saveOrUpdate(user) }
}
