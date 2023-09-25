package com.example.excepcionesexcepcionales.solution.user.secondaryadapter.database

import arrow.core.Either
import com.example.excepcionesexcepcionales.solution.user.domain.Email
import com.example.excepcionesexcepcionales.solution.user.domain.ExistsUserCriteria
import com.example.excepcionesexcepcionales.solution.user.domain.FindUserCriteria
import com.example.excepcionesexcepcionales.solution.user.domain.RepositoryResult
import com.example.excepcionesexcepcionales.solution.user.domain.User
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import com.example.excepcionesexcepcionales.solution.user.domain.UserRepository
import org.springframework.stereotype.Component

@Component
class H2UserRepository(private val jpaRepository: JpaUserRepository) : UserRepository {
    override fun findBy(userId: UserId): User {
        TODO("Not yet implemented")
    }

    override fun existBy(userId: UserId): Boolean {
        TODO("Not yet implemented")
    }

    override fun existBy(email: Email): Boolean = jpaRepository.existsByEmailIgnoreCase(email.value)

    override fun save(user: User) { jpaRepository.save(user.toJpa()) }

    override fun findBySealed(userId: UserId): RepositoryResult<User> {
        TODO("Not yet implemented")
    }

    override fun existBySealed(email: Email): RepositoryResult<Boolean> {
        TODO("Not yet implemented")
    }

    override fun existBySealed(userId: UserId): RepositoryResult<Boolean> {
        TODO("Not yet implemented")
    }

    override fun saveSealed(user: User): RepositoryResult<User> {
        TODO("Not yet implemented")
    }

    override fun find(criteria: FindUserCriteria): Either<Throwable, User> {
        TODO("Not yet implemented")
    }

    override fun exists(criteria: ExistsUserCriteria): Either<Throwable, Boolean> {
        TODO("Not yet implemented")
    }

    override fun eitherSave(user: User): Either<Throwable, Unit> {
        TODO("Not yet implemented")
    }
}
