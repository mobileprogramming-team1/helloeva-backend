package com.helloeva.common.exception

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("[{}] {} {}", e.errorCode.name, request.method, request.requestURI)
        return toResponse(ErrorResponse.of(e.errorCode))
    }

    @ExceptionHandler(BindException::class)
    fun handleBindException(e: BindException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.bindingResult)
        log.warn("[Validation Failed] {} {}, errors={}", request.method, request.requestURI, response.errors)
        return toResponse(response)
    }

    @ExceptionHandler(HandlerMethodValidationException::class, HttpMessageNotReadableException::class)
    fun handleInvalidInput(e: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("[Invalid Input] {} {}, message={}", request.method, request.requestURI, e.message)
        return toResponse(ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(e: MethodArgumentTypeMismatchException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("[Type Mismatch] {} {}, parameter={}", request.method, request.requestURI, e.name)
        return toResponse(ErrorResponse.of(ErrorCode.INVALID_TYPE_VALUE))
    }

    @ExceptionHandler(MissingServletRequestParameterException::class, MissingServletRequestPartException::class)
    fun handleMissingParameter(e: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("[Missing Parameter] {} {}, message={}", request.method, request.requestURI, e.message)
        return toResponse(ErrorResponse.of(ErrorCode.MISSING_REQUEST_PARAMETER))
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowed(e: HttpRequestMethodNotSupportedException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("[Method Not Allowed] {} {}", request.method, request.requestURI)
        return toResponse(ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED))
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNotFound(e: NoResourceFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("[Not Found] {} {}", request.method, request.requestURI)
        return toResponse(ErrorResponse.of(ErrorCode.NOT_FOUND))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("[Internal Server Error] {} {}", request.method, request.requestURI, e)
        return toResponse(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR))
    }

    private fun toResponse(response: ErrorResponse): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(response.status).body(response)
}
