package com.example.excepcionesexcepcionales.session.user.create

import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserCommandHandler
import com.example.excepcionesexcepcionales.session.user.create.fakes.FakeUserRepository
import com.example.excepcionesexcepcionales.session.user.create.mothers.CreateUserRequestBodyMother
import com.example.excepcionesexcepcionales.session.user.domain.User.UserAlreadyExistsException
import com.example.excepcionesexcepcionales.session.user.mothers.CreateUserCommandMother
import com.example.excepcionesexcepcionales.session.user.mothers.UserCreatedEventMother
import com.example.excepcionesexcepcionales.session.user.mothers.UserMother
import com.example.excepcionesexcepcionales.session.user.primaryAdapter.create.CreateUserController
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeClock
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeDomainEventPublisher
import com.example.excepcionesexcepcionales.solution.user.fakes.FakeIdGenerator
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_EMAIL
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_NAME
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_PHONE_NUMBER
import com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors.UserServerErrors.INVALID_SURNAME
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.provider.Arguments
import org.springframework.http.HttpStatus

class CreateUserTest {

    private val repository = FakeUserRepository
    private val publisher = FakeDomainEventPublisher
    private val idGenerator = FakeIdGenerator
    private val clock = FakeClock

    private val handler = CreateUserCommandHandler(repository, publisher)

    private val controller = CreateUserController(handler, idGenerator, clock)

    @BeforeEach
    fun setUp() {
        repository.resetFake()
        publisher.resetFake()
        idGenerator.resetFake()
        clock.resetFake()
    }

    @Test
    fun `should create user and publish event`() {
        // When
        handler.handle(command)

        // Then
        assertTrue { repository.wasPersisted(user) }
        assertTrue { publisher.wasPublished(expectedEvent) }
    }

    @Test
    fun `should throw exception if user already exists`() {
        // Given
        `user already exists`()

        // When
        assertThrows<UserAlreadyExistsException> {
            handler.handle(command)
        }

        // Then
        assertFalse { publisher.wasPublished(expectedEvent) }
    }

//    @ParameterizedTest
//    @MethodSource("validationErrors")
//    fun `should return bad request if email is invalid`(body: CreateUserRequestBody, status: HttpStatus, error: String) {
//        // Given
//        `ids will be generated`()
//        `clock works`()
//
//        // When
//        val result = controller.create(body)
//
//        // Then
//        assertEquals(status, result.statusCode)
//        assertEquals(error, result.body)
//    }

    private fun `user already exists`() {
        repository.save(user)
    }

    private fun `clock works`() {
        clock.shouldGenerate(user.createdOn)
    }

    private fun `ids will be generated`() {
        idGenerator.shouldGenerate(user.id.value)
    }

    companion object {
        private val user = UserMother.incomplete()
        private val command = CreateUserCommandMother.fromUser(user)
        private val expectedEvent = UserCreatedEventMother.fromUser(user)
        private val body = CreateUserRequestBodyMother.fromUser(user)

        @JvmStatic
        fun validationErrors() = Stream.of(
            Arguments.of(
                body.copy(email = "abe"),
                HttpStatus.BAD_REQUEST,
                INVALID_EMAIL
            ),
            Arguments.of(
                body.copy(phoneNumber = "+3a1"),
                HttpStatus.BAD_REQUEST,
                INVALID_PHONE_NUMBER
            ),
            Arguments.of(
                body.copy(phonePrefix = "1bd"),
                HttpStatus.BAD_REQUEST,
                INVALID_PHONE_NUMBER
            ),
            Arguments.of(
                body.copy(name = "    "),
                HttpStatus.BAD_REQUEST,
                INVALID_NAME
            ),
            Arguments.of(
                body.copy(surname = "123412341234123412341234123412341234123412341234123412341234123412341234123412341"),
                HttpStatus.BAD_REQUEST,
                INVALID_SURNAME
            ),
        )
    }
}
