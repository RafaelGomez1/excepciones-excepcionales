package com.example.excepcionesexcepcionales.shared.validation

sealed class Validation<Value> {
    class Success<Value>(val value: Value): Validation<Value>()
    class Failure<Value>(): Validation<Value>()
}
