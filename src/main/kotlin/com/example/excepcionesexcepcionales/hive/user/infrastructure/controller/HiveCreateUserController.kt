package com.example.excepcionesexcepcionales.hive.user.infrastructure.controller

import com.example.excepcionesexcepcionales.hive.user.application.create.initial.CreateUserData
import com.example.excepcionesexcepcionales.hive.user.application.create.initial.HiveImperativeUserCreator
import com.example.excepcionesexcepcionales.hive.user.domain.*
import org.springframework.http.HttpStatus.*
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
class HiveCreateUserController(private val createUser: HiveImperativeUserCreator) {

//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERADMIN', 'ROLE_MANAGER')")
    @PostMapping(value = ["/user"], produces = [APPLICATION_JSON_VALUE])
    @ResponseBody
    fun execute(@RequestBody(required = true) userRequest: UserRequest, creator: String): ResponseEntity<String> {
        return try {
            createUser.execute(
                CreateUserData.fromRawData(
                    name = userRequest.name,
                    email = userRequest.email,
                    role = userRequest.role ?: "BASIC",
                    creator = creator // Hardcoded value for a simplified example
                )
            )
            ResponseEntity.status(CREATED).body("{}")
        } catch (exception: UserExceptions) {
            when (exception) {
                is UserRoleNotAllowedException, is UserInvalidEmailException -> ResponseEntity.badRequest().body(exception.message)
                is UserEmailDuplicatedException -> ResponseEntity.status(CONFLICT).body(exception.message)
                is UserCannotBeSavedException, is UserCannotBeAssignedToRoleException -> ResponseEntity.status(INTERNAL_SERVER_ERROR).body(exception.message)
            }
        } catch (exception: Throwable) {
            ResponseEntity.internalServerError().body("Something went wrong")
        }
    }
}

data class UserRequest(
    val name: String,
    val email: String,
    val role: String?
)
