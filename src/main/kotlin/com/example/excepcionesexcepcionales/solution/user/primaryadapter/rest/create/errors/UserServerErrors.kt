package com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors

object UserServerErrors {

    const val INVALID_EMAIL = "Provided email is not valid"
    const val INVALID_PHONE_NUMBER = "Phone Number is not valid"
    const val INVALID_NAME = "Name is blank or over 50 characters"
    const val INVALID_SURNAME = "Surname is blank or over 80 characters"

    const val USER_ALREADY_EXISTS = "User already exists"
}
