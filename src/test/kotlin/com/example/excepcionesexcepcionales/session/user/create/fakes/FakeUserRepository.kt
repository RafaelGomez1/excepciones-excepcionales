package com.example.excepcionesexcepcionales.session.user.create.fakes

import com.example.excepcionesexcepcionales.session.user.domain.*
import com.example.excepcionesexcepcionales.session.user.domain.ExistsUserCriteria.ByEmail
import com.example.excepcionesexcepcionales.session.user.domain.ExistsUserCriteria.ById
import com.example.excepcionesexcepcionales.session.user.domain.RepositoryResult.RepoSuccess
import com.example.excepcionesexcepcionales.session.user.domain.RepositoryResult.RepoUnknown
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeRepository

object FakeUserRepository : UserRepository, FakeRepository<User> {
    override val elements = mutableListOf<User>()

    override fun findBy(userId: UserId): User = elements.first { it.id == userId }

    override fun existByUserId(userId: UserId): Boolean = elements.any { it.id == userId }

    override fun existByEmail(email: Email): Boolean = elements.any { it.email == email }

    override fun save(user: User) { elements.saveOrUpdate(user) }

    override fun find(criteria: FindUserCriteria): User? {
        TODO("Not yet implemented")
    }

    override fun existsSealed(criteria: ExistsUserCriteria): RepositoryResult<Boolean> =
        runCatching {
            when(criteria) {
                is ByEmail -> elements.any { it.email == criteria.email }
                is ById -> elements.any { it.id == criteria.id }
            }
        }
            .map { result -> RepoSuccess(result) }
            .getOrElse { error -> RepoUnknown(error) }

    override fun saveSealed(user: User): RepositoryResult<Unit> =
        runCatching { elements.saveOrUpdate(user) }
            .map { RepoSuccess(Unit) }
            .getOrElse { error -> RepoUnknown(error) }

    override fun exists(criteria: ExistsUserCriteria): Boolean =
        when (criteria) {
            is ByEmail -> elements.any { it.email == criteria.email }
            is ById -> elements.any { it.id == criteria.id }
        }
}
