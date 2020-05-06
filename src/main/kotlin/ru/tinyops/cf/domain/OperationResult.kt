package ru.tinyops.cf.domain

import arrow.core.Either

typealias OperationResult<R> = Either<OperationError, R>

enum class OperationError {
    ERROR, NOT_FOUND
}
