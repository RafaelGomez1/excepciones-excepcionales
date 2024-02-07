package com.example.excepcionesexcepcionales.hive.user.domain

sealed class UserExceptions(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

data class UserInvalidEmailException(val email: String) : UserExceptions("<$email> is not a valid email")
data class UserRoleNotAllowedException(val userRole: String) : UserExceptions(
    "User with role $userRole is not allowed to perform this action"
)

data class UserEmailDuplicatedException(val email: String) : UserExceptions(
    "There is already a user with email <$email>"
)

data class UserCannotBeSavedException(val name: String, override val cause: Throwable) :
    UserExceptions("<$name> cannot be saved", cause)

data class UserCannotBeAssignedToRoleException(val name: String, val role: Role, override val cause: Throwable) :
    UserExceptions("User <$name> cannot be assigned to role <$role>", cause)

data class UserNotFoundException(val userId: String) : RuntimeException("User with id <$userId> not found")
data class UsernameNotFoundException(val name: String) : RuntimeException("User with username <$name> not found")
