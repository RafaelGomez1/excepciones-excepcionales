package com.example.excepcionesexcepcionales.solution.user.application.verify.imperative

import com.example.excepcionesexcepcionales.shared.event.DomainEventPublisher
import com.example.excepcionesexcepcionales.solution.user.application.find.UserFinder
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUserRepository
import com.example.excepcionesexcepcionales.solution.user.domain.UserId

class ImperativeUserVerifier(
    private val repository: SolutionUserRepository,
    private val publisher: DomainEventPublisher
) {
    private val finder = UserFinder(repository)

    fun invoke(id: UserId) {
        val user = finder.invoke(id)
        val verifiedUser = user.verify()

        repository.save(user)
        publisher.publish(verifiedUser.pullEvents())
    }

}
