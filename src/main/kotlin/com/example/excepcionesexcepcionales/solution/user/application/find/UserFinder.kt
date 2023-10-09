package com.example.excepcionesexcepcionales.solution.user.application.find

import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUser
import com.example.excepcionesexcepcionales.solution.user.domain.UserId
import com.example.excepcionesexcepcionales.solution.user.domain.SolutionUserRepository

class UserFinder(private val repository: SolutionUserRepository) {

    fun invoke(id: UserId): SolutionUser =
        if (!repository.existBy(id)) throw UserDoesNotExistException()
        else repository.findBy(id)
}

class UserDoesNotExistException : Throwable()
