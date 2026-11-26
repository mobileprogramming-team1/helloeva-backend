package com.helloeva.common.security

import com.helloeva.common.exception.ErrorCode
import com.helloeva.common.exception.ErrorResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class ErrorResponseWriter(
    private val jsonMapper: JsonMapper,
) {
    fun write(response: HttpServletResponse, errorCode: ErrorCode) {
        response.status = errorCode.status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()
        jsonMapper.writeValue(response.writer, ErrorResponse.of(errorCode))
    }
}
