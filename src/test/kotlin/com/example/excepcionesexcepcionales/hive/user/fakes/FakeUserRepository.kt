package com.example.excepcionesexcepcionales.hive.user.fakes

import com.example.excepcionesexcepcionales.hive.user.domain.*
import com.example.excepcionesexcepcionales.hive.user.domain.FindUserCriteria.ByEmail
import com.example.excepcionesexcepcionales.hive.user.domain.SearchUserCriteria.All
import com.example.excepcionesexcepcionales.hive.user.domain.SearchUserCriteria.ByUsername
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeRepositoryV2
import com.example.excepcionesexcepcionales.hive.user.domain.ExistUserCriteria.ByEmail as ExistByEmail

object FakeUserRepository : UserRepository, FakeRepositoryV2<Email, User> {
    override val elements = mutableMapOf<Email, User>()
    override val errors = mutableListOf<Throwable>()

    override fun findByEmail(email: String): User? = elements[Email(email)]

    override fun search(criteria: SearchUserCriteria): List<User> =
        when(criteria) {
            All -> elements.values
            is ByUsername -> elements.values.filter { it.name == criteria.username }
        }.toList()

    override fun find(criteria: FindUserCriteria): User? =
        when(criteria) {
            is ByEmail -> elements[criteria.email]
        }

    override fun exist(criteria: ExistUserCriteria): Boolean =
        when(criteria) {
            is ExistByEmail -> elements.contains(criteria.email)
        }

    override fun save(user: User) { elements.saveOrUpdate(user, user.email) }
    override fun delete(user: User) { elements.remove(user.email) }


}
