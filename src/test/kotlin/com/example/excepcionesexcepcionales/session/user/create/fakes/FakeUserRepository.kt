package com.example.excepcionesexcepcionales.session.user.create.fakes

import com.example.excepcionesexcepcionales.session.user.domain.*
import com.example.excepcionesexcepcionales.session.user.domain.ExistsUserCriteria.ByEmail
import com.example.excepcionesexcepcionales.session.user.domain.ExistsUserCriteria.ById
import com.example.excepcionesexcepcionales.session.user.domain.RepositoryResult.RepoSuccess
import com.example.excepcionesexcepcionales.session.user.domain.RepositoryResult.RepoUnknown
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeRepositoryV2

object FakeUserRepository : UserRepository, FakeRepositoryV2<UserId, User> {
    override val elements = mutableMapOf<UserId, User>()
    override val errors = mutableListOf<Throwable>()

    override fun findBy(userId: UserId): User = elements.getValue(userId)

    override fun existByUserId(userId: UserId): Boolean = elements.contains(userId)

    override fun existByEmail(email: Email): Boolean = elements.values.any { it.email == email }

    override fun existsSealed(criteria: ExistsUserCriteria): RepositoryResult<Boolean> =
        runCatching {
            when(criteria) {
                is ByEmail -> elements.values.any { it.email == criteria.email }
                is ById -> elements.contains(criteria.id)
            }
        }
            .map { result -> RepoSuccess(result) }
            .getOrElse { error -> RepoUnknown(error) }

    override fun saveSealed(user: User): RepositoryResult<Unit> =
        runCatching { elements.saveOrUpdate(user, user.id) }
            .map { RepoSuccess(Unit) }
            .getOrElse { error -> RepoUnknown(error) }

    override fun save(user: User) { elements.saveOrUpdate(user, user.id) }

    override fun find(criteria: FindUserCriteria): User? =
        when(criteria) {
            is FindUserCriteria.ById -> elements[criteria.id]
        }

    override fun exists(criteria: ExistsUserCriteria): Boolean =
        when (criteria) {
            is ByEmail -> elements.values.any { it.email == criteria.email }
            is ById -> elements.contains(criteria.id)
        }
}
