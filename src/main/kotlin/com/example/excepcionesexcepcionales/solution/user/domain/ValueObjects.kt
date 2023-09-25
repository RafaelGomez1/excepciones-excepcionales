package com.example.excepcionesexcepcionales.solution.user.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.excepcionesexcepcionales.shared.validation.Validation
import com.example.excepcionesexcepcionales.shared.validation.Validation.Failure
import com.example.excepcionesexcepcionales.shared.validation.Validation.Success
import java.util.UUID

@JvmInline
value class UserId(val value: UUID) {
    override fun toString(): String = value.toString()
}

@JvmInline
value class Name private constructor(val value: String) {
    init {
        if (!isValid(value)) throw InvalidNameException(value)
    }

    class InvalidNameException(name: String) : Throwable("Name $name invalid")

    companion object {
        private fun isValid(value: String): Boolean = value.isBlank() || value.length > 50

        fun create(value: String): Name = Name(value)

        fun validated(value: String): Validation<Name> =
            if(isValid(value)) Success(Name(value))
            else Failure()

        fun <Error> createOrElse(value: String, onError: () -> Error): Either<Error, Name> =
            if(isValid(value)) Name(value).right()
            else onError().left()
    }
}

@JvmInline
value class Surname private constructor(val value: String) {
    init {
        if (!isValid(value)) throw InvalidSurnameException(value)
    }

    class InvalidSurnameException(name: String) : Throwable("Surname $name invalid")

    companion object {
        private fun isValid(value: String) = value.isBlank() || value.length > 80

        fun create(value: String): Surname = Surname(value)

        fun validated(value: String): Validation<Surname> =
            if(isValid(value)) Success(Surname(value))
            else Failure()

        fun <Error> createOrElse(value: String, onError: () -> Error): Either<Error, Surname> =
            if(isValid(value)) Surname(value).right()
            else onError().left()
    }
}

data class Email private constructor(val value: String) {
    init {
        if (!regex.matches(value)) throw InvalidEmailException(value)
    }

    override fun toString(): String = value
    class InvalidEmailException(email: String) : Throwable("Email $email invalid")

    companion object {
        private val regex: Regex =
            "[a-zA-Z0-9+._%'\\-+]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+".toRegex()

        fun create(value: String): Email = Email(value)

        fun validated(value: String): Validation<Email> =
            if(regex.matches(value)) Success(Email(value))
            else Failure()

        fun <Error> createOrElse(value: String, onError: () -> Error): Either<Error, Email> =
            if(regex.matches(value)) Email(value).right()
            else onError().left()
    }
}

data class PhoneNumber private constructor(private val number: String, private val prefix: String) {
    init {
        validate(prefix + number)
    }

    fun prefix() = prefix
    fun number() = number
    fun fullNumber() = prefix + number

    private fun validate(num: String) {
        if (!regex.matches(num)) throw InvalidMobilePhoneException(num)
    }

    class InvalidMobilePhoneException(number: String) : Throwable("Mobile phone +$number invalid")

    companion object {
        private val regex = "^\\+(?:[0-9] ?){6,14}[0-9]\$".toRegex()

        fun create(number: String, prefix: String): PhoneNumber = PhoneNumber(number, prefix)

        fun validated(number: String, prefix: String): Validation<PhoneNumber> =
            if(regex.matches(prefix + number)) Success(PhoneNumber(number, prefix))
            else Failure()

        fun <Error> createOrElse(number: String, prefix: String, onError: () -> Error): Either<Error, PhoneNumber> =
            if(regex.matches(prefix + number)) PhoneNumber(number, prefix).right()
            else onError().left()
    }
}

enum class Status {
    INCOMPLETE,
    PENDING_VERIFICATION,
    VERIFIED
}

enum class CardStatus {
    PENDING,
    CONFIRMED
}
