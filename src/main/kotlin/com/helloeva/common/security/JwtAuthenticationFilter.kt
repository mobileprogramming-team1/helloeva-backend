package com.helloeva.common.security

import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode
import com.helloeva.domain.user.infrastructure.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
    private val errorResponseWriter: ErrorResponseWriter,
    private val publicPaths: Array<String>,
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        request.requestURI in publicPaths

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = resolveToken(request)
        if (token == null) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val claims = jwtTokenProvider.parse(token)
            val user = userRepository.findById(claims.userId).orElse(null)
            if (user == null || user.isTokenRevoked(claims.issuedAt)) {
                throw CustomException(ErrorCode.INVALID_TOKEN)
            }
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(user.id, null, emptyList())
        } catch (e: CustomException) {
            errorResponseWriter.write(response, e.errorCode)
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        if (!header.startsWith(BEARER_PREFIX)) return null
        return header.removePrefix(BEARER_PREFIX).trim().ifBlank { null }
    }

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }
}
