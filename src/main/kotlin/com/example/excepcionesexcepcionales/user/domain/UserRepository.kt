package com.example.excepcionesexcepcionales.user.domain

import arrow.core.Either

interface UserRepository {
    fun findBy(userId: UserId): User
    fun save(user: User)

    fun find(criteria: FindUserCriteria): Either<Throwable, User>
}

sealed interface FindUserCriteria {
    class ById(val id: UserId) : FindUserCriteria
}
