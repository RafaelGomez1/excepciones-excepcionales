package com.example.excepcionesexcepcionales.hive.user.domain

interface UserRepository {
//    fun findAll(): List<User>
//    fun save(user: User)
    fun findByEmail(email: String): User?
//    fun findUsersByUsername(username: String): List<User>
//    fun deleteUser(username: String)

    fun search(criteria: SearchUserCriteria): List<User>
    fun find(criteria: FindUserCriteria): User?
    fun exist(criteria: ExistUserCriteria): Boolean
    fun save(user: User)
    fun delete(user: User)
}

sealed interface SearchUserCriteria {
    object All : SearchUserCriteria
    class ByUsername(val username: String) : SearchUserCriteria
}

sealed interface FindUserCriteria {
    class ByEmail(val email: Email) : FindUserCriteria
}

sealed interface ExistUserCriteria {
    class ByEmail(val email: Email) : ExistUserCriteria
}
