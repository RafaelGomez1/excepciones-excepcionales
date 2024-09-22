package com.example.excepcionesexcepcionales.shared.error

import arrow.core.Either
import arrow.core.Either.Companion.catch
import arrow.core.filterOrElse
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import org.springframework.http.ResponseEntity

fun <Error, VO> withError(block: () -> VO, onError: () -> Error): Either<Error, VO> =
    catch { block() }
        .mapLeft { onError() }

fun <Error> Either<Error, Boolean>.failIfTrue(failure: () -> Error): Either<Error, Boolean> =
        flatMap {
            if (it) failure().left()
            else it.right()
        }

fun <Error> Either<Error, Boolean>.failIfFalse(failure: () -> Error): Either<Error, Boolean> =
        flatMap {
            if (!it) failure().left()
            else it.right()
        }

inline fun <E, R> Either<E, R>.toServerResponse(
    onValidResponse: (R) -> Response<*>,
    onError: (E) -> Response<*>
): Response<*> = fold({ onError(it) }, { onValidResponse(it) })

fun ResponseEntity.BodyBuilder.withoutBody(): Response<*> = body(null)

typealias Response<T> = ResponseEntity<T>
