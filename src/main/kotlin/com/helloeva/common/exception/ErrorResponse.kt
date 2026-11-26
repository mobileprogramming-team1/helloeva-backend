package com.helloeva.common.exception

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.validation.BindingResult

data class ErrorResponse(
    val status: Int,
    val code: String,
    val message: String,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val errors: List<FieldError>? = null,
) {
    data class FieldError(
        val field: String,
        val message: String?,
    )

    companion object {
        fun of(errorCode: ErrorCode): ErrorResponse =
            ErrorResponse(
                status = errorCode.status.value(),
                code = errorCode.name,
                message = errorCode.message,
            )

        fun of(errorCode: ErrorCode, bindingResult: BindingResult): ErrorResponse =
            of(errorCode).copy(
                errors = bindingResult.fieldErrors.map { FieldError(it.field, it.defaultMessage) },
            )
    }
}
