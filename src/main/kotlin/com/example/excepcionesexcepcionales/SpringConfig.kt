package com.example.excepcionesexcepcionales

import com.example.excepcionesexcepcionales.session.user.application.create.CreateUserCommandHandler
import com.example.excepcionesexcepcionales.session.user.domain.UserRepository
import com.example.excepcionesexcepcionales.shared.clock.Clock
import com.example.excepcionesexcepcionales.shared.clock.UTCClock
import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.shared.event.InMemoryEventPublisher
import com.example.excepcionesexcepcionales.shared.id.IdGenerator
import com.example.excepcionesexcepcionales.shared.id.UUIDGenerator
import com.example.excepcionesexcepcionales.solution.user.application.create.functional.FunctionalCreateUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.application.create.imperative.ImperativeCreateUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.application.create.sealed.SealedCreateUserCommandHandler
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringConfig {

    @Bean fun publisher(): DomainEventPublisher = InMemoryEventPublisher()
    @Bean fun clock(): Clock = UTCClock()
    @Bean fun idGenerator(): IdGenerator = UUIDGenerator()

    @Bean
    fun createUserCommandHandler(
        repository: UserRepository,
        publisher: DomainEventPublisher
    ): CreateUserCommandHandler = CreateUserCommandHandler(repository, publisher)

    @Bean
    fun functionalCreateUserCommandHandler(
        repository: SolutionUserRepository,
        publisher: DomainEventPublisher
    ): FunctionalCreateUserCommandHandler = FunctionalCreateUserCommandHandler(repository, publisher)

    @Bean
    fun imperativeCreateUserCommandHandler(
        repository: SolutionUserRepository,
        publisher: DomainEventPublisher
    ): ImperativeCreateUserCommandHandler = ImperativeCreateUserCommandHandler(repository, publisher)

    @Bean
    fun sealedCreateUserCommandHandler(
        repository: SolutionUserRepository,
        publisher: DomainEventPublisher
    ): SealedCreateUserCommandHandler = SealedCreateUserCommandHandler(repository, publisher)
}
