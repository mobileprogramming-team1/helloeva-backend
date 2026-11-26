package com.helloeva.common.security

import com.helloeva.common.exception.CustomException
import com.helloeva.common.exception.ErrorCode
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val properties: JwtProperties,
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(properties.secret.toByteArray())

    fun createAccessToken(userId: Long): String {
        val now = Date()
        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(Date(now.time + properties.accessTokenValidity.toMillis()))
            .signWith(key)
            .compact()
    }

    fun parse(token: String): TokenClaims {
        val claims = try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        } catch (e: ExpiredJwtException) {
            throw CustomException(ErrorCode.EXPIRED_TOKEN)
        } catch (e: JwtException) {
            throw CustomException(ErrorCode.INVALID_TOKEN)
        } catch (e: IllegalArgumentException) {
            throw CustomException(ErrorCode.INVALID_TOKEN)
        }

        val userId = claims.subject?.toLongOrNull() ?: throw CustomException(ErrorCode.INVALID_TOKEN)
        val issuedAt = claims.issuedAt ?: throw CustomException(ErrorCode.INVALID_TOKEN)

        return TokenClaims(
            userId = userId,
            issuedAt = LocalDateTime.ofInstant(issuedAt.toInstant(), ZoneId.systemDefault()),
        )
    }
}
