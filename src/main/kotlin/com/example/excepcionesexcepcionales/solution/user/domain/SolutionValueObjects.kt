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
        if (isNotValid(value)) throw InvalidNameException(value)
    }

    class InvalidNameException(name: String) : RuntimeException("Name $name invalid")

    companion object {
        private fun isNotValid(value: String): Boolean = value.isBlank() || value.length > 50

        fun create(value: String): Name = Name(value)

        fun validated(value: String): Validation<Name> =
            if(isNotValid(value)) Failure()
            else Success(Name(value))

        fun <Error> createOrElse(value: String, onError: () -> Error): Either<Error, Name> =
            if(isNotValid(value)) onError().left()
            else Name(value).right()
    }
}

@JvmInline
value class Surname private constructor(val value: String) {
    init {
        if (isNotValid(value)) throw InvalidSurnameException(value)
    }

    class InvalidSurnameException(name: String) : RuntimeException("Surname $name invalid")

    companion object {
        private fun isNotValid(value: String) = value.isBlank() || value.length > 80

        fun create(value: String): Surname = Surname(value)

        fun validated(value: String): Validation<Surname> =
            if(isNotValid(value)) Failure()
            else Success(Surname(value))

        fun <Error> createOrElse(value: String, onError: () -> Error): Either<Error, Surname> =
            if(isNotValid(value)) onError().left()
            else Surname(value).right()
    }
}

data class Email private constructor(val value: String) {
    init {
        if (!regex.matches(value)) throw InvalidEmailException(value)
    }

    override fun toString(): String = value
    class InvalidEmailException(email: String) : RuntimeException("Email $email invalid")

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
        if (!regex.matches(num)) throw InvalidPhoneNumberException(num)
    }

    class InvalidPhoneNumberException(number: String) : RuntimeException("Mobile phone +$number invalid")

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
