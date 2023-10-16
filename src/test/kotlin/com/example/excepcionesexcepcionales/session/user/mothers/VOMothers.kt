package com.example.excepcionesexcepcionales.session.user.mothers

import com.example.excepcionesexcepcionales.faker.Fake
import com.example.excepcionesexcepcionales.session.user.domain.Email
import com.example.excepcionesexcepcionales.session.user.domain.Name
import com.example.excepcionesexcepcionales.session.user.domain.PhoneNumber
import com.example.excepcionesexcepcionales.session.user.domain.Surname
import com.example.excepcionesexcepcionales.session.user.domain.UserId
import java.util.UUID

object UserIdMother {
    fun random(value: UUID = UUID.randomUUID()) = UserId(value)
}

object NameMother {
    fun random(value: String = Fake.faker.name.firstName()) = Name.create(value)
}

object SurnameMother {
    fun random(value: String = Fake.faker.name.lastName()) = Surname.create(value)
}

object EmailMother {
    fun random(value: String = "email.random@gmail.com") = Email.create(value)
}

object PhoneNumberMother {
    fun random(prefix: String = "+38", number: String = "677643621") = PhoneNumber.create(number, prefix)
}
