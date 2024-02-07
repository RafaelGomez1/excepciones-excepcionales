package com.example.excepcionesexcepcionales.solution.user.primaryadapter.rest.create.errors

object UserServerErrors {

    const val INVALID_EMAIL = "Provided email is not valid"
    const val INVALID_PHONE_NUMBER = "Phone Number is not valid"
    const val INVALID_NAME = "Name is blank or over 50 characters"
    const val INVALID_SURNAME = "Surname is blank or over 80 characters"

    const val INVALID_USER_IDENTIFIER = "Invalid Identifier"
    const val DOCUMENTS_NOT_VERIFIED = "Cannot verify user until all required documents are verified"
    const val INCOMPLETE_USER = "Cannot verify user until all required documents are uploaded"
    const val USER_ALREADY_VERIFIED = "User is already verified"
    const val PAYMENT_METHOD_NOT_CONFIRMED = "User needs to confirm payment method in order to be verified"

    const val USER_ALREADY_EXISTS = "User already exists"
    const val USER_DOES_NOT_EXIST = "User does not exist"
}
