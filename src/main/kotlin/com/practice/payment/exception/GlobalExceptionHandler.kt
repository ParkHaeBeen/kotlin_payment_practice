package com.practice.payment.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(PaymentException::class)
    fun handlePaymentException(e: PaymentException): ErrorResponse {
        log.error(e) { "${e.errorCode} is occurred. " }
        log.error(e) { "${e.errorCode.name} is occurred. " }
        return ErrorResponse(e.errorCode)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ErrorResponse {
        log.error(e) { "Exception is occurred. " }
        return ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR)
    }
}

class ErrorResponse(
    val errorCode: ErrorCode,
    val errorMessage: String = errorCode.errorMessage,
)