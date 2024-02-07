package com.example.excepcionesexcepcionales.hive.user.application.create.initial

import com.example.excepcionesexcepcionales.hive.user.domain.*
import com.example.excepcionesexcepcionales.hive.user.domain.Role.*
import org.springframework.stereotype.Component

@Component
class HiveImperativeUserCreator(private val userRepository: UserRepository) {

    fun execute(userData: CreateUserData) {
        validateUniqueEmail(userData.email)
        validateAllowedRoles(userData)

        val user = User(
            name = userData.name,
            email = Email(userData.email),
            role = userData.role
        )

        userRepository.save(user)
    }

    private fun validateUniqueEmail(email: String) {
        userRepository.findByEmail(email)?.let {
            throw UserEmailDuplicatedException(email)
        }
    }

    private fun validateAllowedRoles(userData: CreateUserData) =
        when (userData.creator) {
            SUPERADMIN, ADMIN -> listOf(ADMIN, MANAGER, BASIC)
            MANAGER -> listOf(MANAGER, BASIC)
            BASIC -> throw UserRoleNotAllowedException(userData.creator.name)
        }.let {
            if (!it.contains(userData.role)) throw UserRoleNotAllowedException(userData.role.name)
        }
}

data class CreateUserData(
    val creator: Role,
    val name: String,
    val email: String,
    val role: Role
) {
    companion object {
        fun fromRawData(creator: String, name: String, email: String, role: String) =
            CreateUserData(
                creator = Role.valueOf(creator.uppercase()),
                name = name,
                email = email,
                role = Role.valueOf(role.uppercase())
            )
    }
}
