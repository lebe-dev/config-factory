package ru.tinyops.cf.util

import arrow.core.Either
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import ru.tinyops.cf.domain.OperationError
import ru.tinyops.cf.domain.OperationResult

fun <R> assertRightResult(result: OperationResult<R>, body: (R) -> Unit) {
    assertTrue(result.isRight())

    when(result) {
        is Either.Right -> body(result.b)
        is Either.Left -> throw Exception("right result expected")
    }
}

fun <R> assertLeftResult(result: OperationResult<R>) {
    assertTrue(result.isLeft())
}
