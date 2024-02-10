package com.example.excepcionesexcepcionales.session.user.secondaryadapter.database

import arrow.core.Either
import arrow.core.Either.Companion.catch
import com.example.excepcionesexcepcionales.session.user.domain.CardStatus
import com.example.excepcionesexcepcionales.session.user.domain.Email
import com.example.excepcionesexcepcionales.session.user.domain.ExistsUserCriteria
import com.example.excepcionesexcepcionales.session.user.domain.ExistsUserCriteria.ByEmail
import com.example.excepcionesexcepcionales.session.user.domain.ExistsUserCriteria.ById
import com.example.excepcionesexcepcionales.session.user.domain.FindUserCriteria
import com.example.excepcionesexcepcionales.session.user.domain.Name
import com.example.excepcionesexcepcionales.session.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.session.user.domain.RepositoryResult
import com.example.excepcionesexcepcionales.session.user.domain.RepositoryResult.RepoSuccess
import com.example.excepcionesexcepcionales.session.user.domain.RepositoryResult.RepoUnknown
import com.example.excepcionesexcepcionales.session.user.domain.Status
import com.example.excepcionesexcepcionales.session.user.domain.Surname
import com.example.excepcionesexcepcionales.session.user.domain.User
import com.example.excepcionesexcepcionales.session.user.domain.UserId
import com.example.excepcionesexcepcionales.session.user.domain.UserRepository
import com.example.excepcionesexcepcionales.solution.user.secondaryadapter.database.JpaUser
import com.example.excepcionesexcepcionales.solution.user.secondaryadapter.database.JpaUserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class H2UserRepository(private val jpaRepository: JpaUserRepository) : UserRepository {
    override fun findBy(userId: UserId): User =
        jpaRepository.findById(userId.value)
            .map { it.toDomain() }
            .orElseThrow()

    override fun existByUserId(userId: UserId): Boolean = jpaRepository.existsById(userId.value)

    override fun existByEmail(email: Email): Boolean = jpaRepository.existsByEmailIgnoreCase(email.value)

    override fun save(user: User) { jpaRepository.save(user.toJpa()) }

    override fun find(criteria: FindUserCriteria): User? =
        when(criteria) {
            is FindUserCriteria.ById -> jpaRepository.findByIdOrNull(criteria.id.value)
        }?.toDomain()


    // Methods for the Sealed Section
    override fun existsSealed(criteria: ExistsUserCriteria): RepositoryResult<Boolean> =
        runCatching {
            when(criteria) {
                is ByEmail -> jpaRepository.existsByEmailIgnoreCase(criteria.email.value)
                is ById -> jpaRepository.existsById(criteria.id.value)
            }
        }
            .map { result -> RepoSuccess(result) }
            .getOrElse { error -> RepoUnknown(error) }

    override fun saveSealed(user: User): RepositoryResult<Unit> =
        runCatching { jpaRepository.save(user.toJpa()) }
            .map { RepoSuccess(Unit) }
            .getOrElse { error -> RepoUnknown(error) }

    override fun exists(criteria: ExistsUserCriteria): Boolean =
        when(criteria) {
            is ByEmail -> jpaRepository.existsByEmailIgnoreCase(criteria.email.value)
            is ById -> jpaRepository.existsById(criteria.id.value)
        }
}

internal fun JpaUser.toDomain(): User = User(
    id = UserId(value = id),
    email = Email.create(value = email),
    phoneNumber = PhoneNumber.create(
        number = phoneNumber,
        prefix = phonePrefix
    ),
    createdOn = createdOn,
    name = Name.create(name),
    surname = Surname.create(surname),
    documents = setOf(),
    status = Status.valueOf(status),
    cardStatus = CardStatus.valueOf(cardStatus)
)

internal fun User.toJpa() = JpaUser(
    id = id.value,
    email = email.value,
    phoneNumber = phoneNumber.number(),
    phonePrefix = phoneNumber.prefix(),
    createdOn = createdOn,
    name = name.value,
    surname = surname.value,
    status = status.name,
    cardStatus = cardStatus.name
)
