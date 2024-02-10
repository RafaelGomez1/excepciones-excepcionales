package com.example.excepcionesexcepcionales.session.user.verify

import com.example.excepcionesexcepcionales.session.user.application.find.UserDoesNotExistException
import com.example.excepcionesexcepcionales.session.user.application.verify.VerifyUserCommandHandler
import com.example.excepcionesexcepcionales.session.user.domain.User.CardStatusNotConfirmedException
import com.example.excepcionesexcepcionales.session.user.domain.User.NotAllDocumentVerifiedException
import com.example.excepcionesexcepcionales.session.user.domain.User.UserAlreadyVerifiedException
import com.example.excepcionesexcepcionales.session.user.domain.User.UserStatusCannotBeVerifiedException
import com.example.excepcionesexcepcionales.session.user.mothers.UserMother
import com.example.excepcionesexcepcionales.session.user.mothers.VerifyUserCommandMother
import com.example.excepcionesexcepcionales.session.user.primaryAdapter.verify.VerifyUserController
import com.example.excepcionesexcepcionales.session.user.primaryAdapter.verify.VerifyUserController.VerifyUserControllerExceptionHandler
import java.util.stream.Stream
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
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

@WebMvcTest(controllers = [VerifyUserController::class])
@ContextConfiguration(classes = [VerifyUserController::class, VerifyUserControllerExceptionHandler::class])
class VerifyUserExceptionHandlerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var handler: VerifyUserCommandHandler

    @ParameterizedTest
    @MethodSource("verifyUserErrors")
    fun `should handle thrown exceptions`(exception: Throwable, statusResultMatcher: ResultMatcher) {
        // Given
        whenever(handler.handle(command)).thenThrow(exception)

        // When
        mockMvc.perform(
            MockMvcRequestBuilders
                .patch("/users/${user.id.value}/verify")
                .contentType(APPLICATION_JSON)
        )
            .andExpect(statusResultMatcher)
            .andReturn()
    }

    companion object {
        private val user = UserMother.random()
        private val command = VerifyUserCommandMother.fromUser(user)

        @JvmStatic
        fun verifyUserErrors() = Stream.of(
            Arguments.of(
                UserDoesNotExistException(),
                status().isUnprocessableEntity,
            ),
            Arguments.of(
                IllegalArgumentException(),
                status().isBadRequest
            ),
            Arguments.of(
                NotAllDocumentVerifiedException(),
                status().isUnprocessableEntity,
            ),
            Arguments.of(
                UserStatusCannotBeVerifiedException(),
                status().isUnprocessableEntity,
            ),
            Arguments.of(
                UserAlreadyVerifiedException(),
                status().isUnprocessableEntity,
            ),
            Arguments.of(
                CardStatusNotConfirmedException(),
                status().isUnprocessableEntity,
            )
        )
    }
}
