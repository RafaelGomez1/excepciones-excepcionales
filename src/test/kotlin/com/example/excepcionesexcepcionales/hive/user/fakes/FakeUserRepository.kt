package com.example.excepcionesexcepcionales.hive.user.fakes

import com.example.excepcionesexcepcionales.hive.user.domain.*
import com.example.excepcionesexcepcionales.hive.user.domain.FindUserCriteria.ByEmail
import com.example.excepcionesexcepcionales.hive.user.domain.SearchUserCriteria.All
import com.example.excepcionesexcepcionales.hive.user.domain.SearchUserCriteria.ByUsername
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeRepository

object FakeUserRepository : UserRepository, FakeRepository<User> {
    override val elements = mutableListOf<User>()

    override fun findByEmail(email: String): User? = elements.firstOrNull { it.email.value == email }

    override fun search(criteria: SearchUserCriteria): List<User> =
        when(criteria) {
            All -> elements
            is ByUsername -> elements.filter { it.name == criteria.username }
        }

    override fun find(criteria: FindUserCriteria): User? =
        when(criteria) {
            is ByEmail -> elements.firstOrNull { it.email == criteria.email }
        }

    override fun exist(criteria: ExistUserCriteria): Boolean =
        when(criteria) {
            is ExistUserCriteria.ByEmail -> elements.any { it.email == criteria.email }
        }

    override fun save(user: User) { elements.add(user) }
    override fun delete(user: User) { elements.removeIf { it.email == user.email } }

}
