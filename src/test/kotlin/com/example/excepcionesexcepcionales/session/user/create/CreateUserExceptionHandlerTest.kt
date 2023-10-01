package com.example.excepcionesexcepcionales.session.user.create

import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserCommandHandler
import com.example.excepcionesexcepcionales.session.user.primaryAdapter.create.CreateUserController
import com.example.excepcionesexcepcionales.session.user.primaryAdapter.create.CreateUserController.CreateUserControllerExceptionHandler
import com.example.excepcionesexcepcionales.shared.clock.Clock
import com.example.excepcionesexcepcionales.shared.id.IdGenerator
import com.example.excepcionesexcepcionales.solution.user.application.create.imperative.ImperativeCreateUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.domain.Email.InvalidEmailException
import com.example.excepcionesexcepcionales.solution.user.domain.Name.InvalidNameException
import com.example.excepcionesexcepcionales.solution.user.domain.PhoneNumber.InvalidPhoneNumberException
import com.example.excepcionesexcepcionales.solution.user.domain.Surname.InvalidSurnameException
import com.example.excepcionesexcepcionales.solution.user.domain.User.UserAlreadyExistsException
import com.example.excepcionesexcepcionales.solution.user.mothers.CreateUserCommandMother
import com.example.excepcionesexcepcionales.solution.user.mothers.ImperativeCreateUserCommandMother
import com.example.excepcionesexcepcionales.solution.user.mothers.UserMother
import java.util.stream.Stream
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [CreateUserController::class])
@ContextConfiguration(classes = [CreateUserController::class, CreateUserControllerExceptionHandler::class])
internal class CreateUserExceptionHandlerTest {

    @Autowired private lateinit var mockMvc: MockMvc

    @MockBean private lateinit var handler: CreateUserCommandHandler
    @MockBean private lateinit var idGenerator: IdGenerator
    @MockBean private lateinit var clock: Clock

    @Test
    fun `should create a user successfully`() {
        // Given
        whenever(idGenerator.generate()).thenReturn(user.id.value)
        whenever(clock.now()).thenReturn(user.createdOn)
        doNothing().`when`(handler).handle(command)

        // When
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/users")
                .contentType(APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)
            .andReturn()
    }

    @ParameterizedTest
    @MethodSource("createUserErrors")
    fun `should handle thrown exceptions`(exception: Throwable, statusResultMatcher: ResultMatcher) {
        // Given
        whenever(idGenerator.generate()).thenReturn(user.id.value)
        whenever(clock.now()).thenReturn(user.createdOn)
        whenever(handler.handle(command)).thenThrow(exception)

        // When
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/users")
                .contentType(APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(statusResultMatcher)
            .andReturn()
    }

    companion object {
        private val user = UserMother.random()
        private val command = CreateUserCommandMother.fromUser(user)

        private val requestBody = """
            {
              "email": "${user.email}",
              "phoneNumber": "${user.phoneNumber.number()}",
              "phonePrefix": "${user.phoneNumber.prefix()}",
              "name": "${user.name.value}",
              "surname": "${user.surname.value}"
            }
        """.trimIndent()

        @JvmStatic
        fun createUserErrors() = Stream.of(
            Arguments.of(
                UserAlreadyExistsException(),
                status().isConflict,
            ),
            Arguments.of(
                InvalidNameException(user.name.value),
                status().isBadRequest
            ),
            Arguments.of(
                InvalidSurnameException(user.name.value),
                status().isBadRequest
            ),
            Arguments.of(
                InvalidEmailException(user.email.value),
                status().isBadRequest
            ),
            Arguments.of(
                InvalidPhoneNumberException(user.phoneNumber.fullNumber()),
                status().isBadRequest
            )
        )
    }
}
