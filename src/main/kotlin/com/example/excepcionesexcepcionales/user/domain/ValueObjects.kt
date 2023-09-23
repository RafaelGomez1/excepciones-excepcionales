package com.example.excepcionesexcepcionales.user.domain

import java.util.UUID

@JvmInline
value class UserId(val value: UUID) {
    override fun toString(): String = value.toString()
}

@JvmInline
value class Name(val value: String) {
    init {
        if (value.isBlank() || value.length > 50) throw InvalidNameException(value)
    }

    class InvalidNameException(name: String) : RuntimeException("Name $name invalid")
}

@JvmInline
value class Surname(val value: String) {
    init {
        if (value.isBlank() || value.length > 80) throw InvalidSurnameException(value)
    }

    class InvalidSurnameException(name: String) : RuntimeException("Surname $name invalid")
}

data class Email(val value: String) {
    private val regex: Regex =
        "[a-zA-Z0-9+._%'\\-+]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+".toRegex()

    init {
        validate()
    }

    private fun validate() {
        if (!regex.matches(value)) throw InvalidEmailException(value)
    }

    class InvalidEmailException(email: String) : RuntimeException("Email $email invalid")
}

data class MobilePhone(private val number: String, private val prefix: String) {
    private val regex = "^\\+(?:[0-9] ?){6,14}[0-9]\$".toRegex()

    init {
        validate(prefix + number)
    }

    fun prefix() = prefix
    fun number() = number
    fun fullNumber() = prefix + number

    private fun validate(num: String) {
        if (!regex.matches(num)) throw InvalidMobilePhoneException(num)
    }

    class InvalidMobilePhoneException(number: String) : RuntimeException("Mobile phone +$number invalid")
}

enum class Status {
    INCOMPLETE,
    PENDING_VERIFICATION,
    VERIFIED
}
