package com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create

data class CreateUserRequestBody(
    val email: String,
    val phoneNumber: String,
    val phonePrefix: String,
    val name: String,
    val surname: String
)
